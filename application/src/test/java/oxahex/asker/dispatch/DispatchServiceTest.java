package oxahex.asker.dispatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskDomainService;
import oxahex.asker.domain.ask.AskType;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.type.ServiceError;
import oxahex.asker.mock.MockUser;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest extends MockUser {

  @InjectMocks
  private DispatchService dispatchService;

  @Mock
  private UserDomainService userDomainService;

  @Mock
  private AskDomainService askDomainService;

  @Mock
  AnswerDomainService answerDomainService;

  @Mock
  private DispatchDomainService dispatchDomainService;

  @Test
  @DisplayName("질문 생성 - 성공 - 로그인하지 않은 경우에도 특정 유저에게 질문을 할 수 있습니다.")
  public void dispatch_ask_success_without_login() throws Exception {

    // given
    AuthUser authUser = null;
    AskReqDto askReqDto = new AskReqDto();
    askReqDto.setAnswerUserId(2L);
    askReqDto.setContents("2L 유저에게 익명 유저가 보내는 질문?");

    // stub 1:
    Ask ask = Ask.builder()
        .id(1L)
        .askUser(null)
        .contents("2L 유저에게 익명 유저가 보내는 질문?")
        .askType(AskType.USER)
        .build();
    given(askDomainService.createAsk(any(), anyString()))
        .willReturn(ask);

    // stub 2:
    User answerUser = mockUser(2L, "answerer", "1234567890");
    Dispatch dispatch = Dispatch.builder()
        .ask(ask)
        .answerUser(answerUser)
        .build();
    given(dispatchDomainService.createAskDispatch(anyLong(), any(Ask.class)))
        .willReturn(dispatch);

    // when
    Ask result = dispatchService.dispatchAsk(null, askReqDto);

    // then
    Assertions.assertEquals(result.getId(), 1L);
    Assertions.assertNull(result.getAskUser());
  }

  @Test
  @DisplayName("질문 생성 - 성공 - 로그인 유저의 경우 특정 유저에게 질문 시 로그인 유저의 정보를 저장한다.")
  public void dispatch_ask_success_with_login_user() throws Exception {

    // given: 질문자 정보 존재
    User askUser = mockUser(1L, "asker", "1234567890");
    User answerUser = mockUser(2L, "answerer", "1234567890");

    AuthUser authUser = new AuthUser(askUser);
    AskReqDto askReqDto = new AskReqDto();
    askReqDto.setAnswerUserId(2L);
    askReqDto.setContents("2L 유저에게 1L 유저가 보내는 질문?");

    // stub 1: 질문 생성 성공
    Ask ask = Ask.builder()
        .id(1L)
        .askUser(authUser.getUser())
        .contents(askReqDto.getContents())
        .askType(AskType.USER)
        .build();
    given(askDomainService.createAsk(any(), anyString()))
        .willReturn(ask);

    // stub 2: 질문 내역 생성 성공
    Dispatch dispatch = Dispatch.builder()
        .ask(ask)
        .answerUser(answerUser)
        .build();
    given(dispatchDomainService.createAskDispatch(anyLong(), any(Ask.class)))
        .willReturn(dispatch);

    // when
    Ask result = dispatchService.dispatchAsk(authUser, askReqDto);

    // then
    Assertions.assertEquals(result.getId(), 1L);
    Assertions.assertEquals(result.getAskUser().getId(), 1L);
  }

  @Test
  @DisplayName("답변 생성 - 성공")
  public void dispatch_answer_success() throws Exception {

    // given
    // 답변할 유저 정보
    User answerUser = mockUser(1L, "로그인 유저", "1234567890");
    // 요청 데이터
    AnswerReqDto answerReqDto = new AnswerReqDto();
    answerReqDto.setDispatchId(2L);
    answerReqDto.setContents("3L 질문에 대한 답변");
    // 질문
    Ask ask = Ask.builder()
        .id(3L).contents("질문 내용").build();
    // 질문 전송 내역
    Dispatch dispatch = Dispatch.builder()
        .answerUser(answerUser)
        .ask(ask)
        .build();

    // Dispatch 정보 가져옴
    given(dispatchDomainService.findDispatch(anyLong()))
        .willReturn(dispatch);

    // 질문에 대한 답변 생성
    Answer answer = Answer.builder()
        .answerUser(answerUser)
        .ask(ask)
        .contents(answerReqDto.getContents())
        .build();
    given(answerDomainService.createAnswer(any(Dispatch.class), anyString()))
        .willReturn(answer);

    // when
    Answer createdAnswer = dispatchService.dispatchAnswer(answerUser, answerReqDto);

    // then
    // 생성된 답변의 작성자 ID가 answerUser ID와 같음
    Assertions.assertEquals(createdAnswer.getAnswerUser().getId(), answerUser.getId());
    // 생성된 답변의 내용이 요청한 답변 내용과 같음
    Assertions.assertEquals(createdAnswer.getContents(), answerReqDto.getContents());
  }

  @Test
  @DisplayName("답변 생성 실패 - 본인에게 온 질문이 아닌 경우 답변할 수 없습니다.")
  public void dispatch_answer_failure_answer_user() throws Exception {

    // given
    // 답변할 유저 정보
    User answerUser = mockUser(1L, "로그인 유저", "1234567890");
    User otherUser = mockUser(2L, "다른 유저", "1234567890");
    // 요청 데이터
    AnswerReqDto answerReqDto = new AnswerReqDto();
    answerReqDto.setDispatchId(2L);
    answerReqDto.setContents("3L 질문에 대한 답변");
    // 질문
    Ask ask = Ask.builder()
        .id(3L).contents("질문 내용").build();
    // 질문 전송 내역: 요청 유저와 다른 유저에게 보낸 질문 전송 내역
    Dispatch dispatch = Dispatch.builder()
        .answerUser(otherUser)
        .ask(ask)
        .build();

    // Dispatch 정보 가져옴
    given(dispatchDomainService.findDispatch(anyLong()))
        .willReturn(dispatch);

    // when
    ServiceException exception = Assertions.assertThrows(ServiceException.class,
        () -> dispatchService.dispatchAnswer(answerUser, answerReqDto));

    // then
    Assertions.assertEquals(exception.getHttpStatus(),
        ServiceError.NO_AUTHORITY_TO_ANSWER.getHttpStatus());
    Assertions.assertEquals(exception.getErrorMessage(),
        ServiceError.NO_AUTHORITY_TO_ANSWER.getErrorMessage());
  }
}
