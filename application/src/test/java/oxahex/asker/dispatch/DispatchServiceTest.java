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
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskDomainService;
import oxahex.asker.domain.ask.AskType;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;
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

    // stub 1:
    Ask ask = Ask.builder()
        .id(1L)
        .askUser(authUser.getUser())
        .contents(askReqDto.getContents())
        .askType(AskType.USER)
        .build();
    given(askDomainService.createAsk(any(), anyString()))
        .willReturn(ask);

    // stub 2:
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
}
