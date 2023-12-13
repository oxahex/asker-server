package oxahex.asker.dispatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.dispatch.dto.AnswerDto.PostedAnswersDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskType;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class AnswerControllerTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private DispatchService dispatchService;

  @MockBean
  private AnswerService answerService;

  private User askUser;
  private User answerUser;
  private User otherUser;
  private Dispatch dispatch;

  @BeforeEach
  public void setup() {
    // 유저
    this.askUser = userRepository.save(newUser("asker", "1234567890"));
    this.answerUser = userRepository.save(newUser("answerer", "1234567890"));
    this.otherUser = userRepository.save(newUser("other", "1234567890"));
  }

  @Test
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("답변 성공 - 로그인 유저는 자신에게 온 질문에 답변할 수 있다.")
  public void answer_with_login_user_success() throws Exception {

    // given
    AnswerReqDto answerReqDto = new AnswerReqDto();
    answerReqDto.setAskId(1L);
    answerReqDto.setContents("asker 유저의 질문에 대한 answerer 유저의 답변");

    Ask ask = Ask.builder()
        .id(1L)
        .askType(AskType.USER)
        .askUser(askUser)
        .contents("asker유저가 answerer 유저에게 보내는 질문")
        .build();

    Answer answer = Answer.builder()
        .id(1L)
        .answerUser(answerUser)
        .ask(ask)
        .build();

    AnswerInfoDto answerInfo = AnswerDto.fromEntityToAnswerInfo(answer);

    given(dispatchService.dispatchAnswer(any(User.class), any(AnswerReqDto.class)))
        .willReturn(answerInfo);

    String requestBody = objectMapper.writeValueAsString(answerReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/answers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    // then
    resultActions.andExpect(status().isCreated());
  }

  @Test
  @DisplayName("답변 실패 - 로그인하지 않은 유저는 접근할 수 없습니다.")
  public void answer_without_login_failure() throws Exception {

    // given
    AnswerReqDto answerReqDto = new AnswerReqDto();
    answerReqDto.setAskId(1L);
    answerReqDto.setContents("asker 유저의 질문에 대한 answerer 유저의 답변");

    String requestBody = objectMapper.writeValueAsString(answerReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/answers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("답변 내역 조회 API 접근 성공 - 비로그인 유저의 경우 답변 내역 조회 API에 접근할 수 있다.")
  public void getUserAnswers_success_without_login() throws Exception {

    // given: 작성한 답변 없음
    given(answerService.getPostedAnswers(anyLong(), any(SortType.class)))
        .willReturn(new PostedAnswersDto());

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/answers")
        .param("userId", answerUser.getId().toString())
        .param("sort", SortType.DESC.getCondition())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("답변 내역 조회 API 접근 성공 - 로그인 유저의 경우 답변 내역 조회 API에 접근할 수 있다.")
  public void getUserAnswers_success_with_login_user() throws Exception {

    // given: 작성한 답변 없음
    given(answerService.getPostedAnswers(anyLong(), any(SortType.class)))
        .willReturn(new PostedAnswersDto());

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/answers")
        .param("userId", answerUser.getId().toString())
        .param("sort", SortType.DESC.getCondition())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("답변 내역 조회 시 작성한 답변이 없는 경우 Empty List를 반환한다.")
  public void getUserAnswers_success_if_null() throws Exception {

    // given: 받은 질문 없음
    PostedAnswersDto postedAnswersDto =
        AnswerDto.fromEntityToPostedAnsweredDto(answerUser, new ArrayList<Answer>());

    // given: 작성한 답변 없음
    given(answerService.getPostedAnswers(anyLong(), any(SortType.class)))
        .willReturn(postedAnswersDto);

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/answers")
        .param("userId", answerUser.getId().toString())
        .param("sort", SortType.DESC.getCondition())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.answerUser.id").value(answerUser.getId()));
    resultActions.andExpect(jsonPath("$.data.answerUser.name").value(answerUser.getName()));
    // 질문 목록은 Empty List
    resultActions.andExpect(jsonPath("$.data.answers").isArray());
    resultActions.andExpect(jsonPath("$.data.answers").isEmpty());
  }
}
