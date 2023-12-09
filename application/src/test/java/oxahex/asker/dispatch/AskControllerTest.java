package oxahex.asker.dispatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskType;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class AskControllerTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private DispatchService dispatchService;

  private User askUser;
  private User answerUser;

  @BeforeEach
  public void setup() {
    this.askUser = userRepository.save(newUser("asker", "1234567890"));
    this.answerUser = userRepository.save(newUser("answerer", "1234567890"));
  }

  @Test
  @DisplayName("질문 요청 성공 - 로그인하지 않은 유저라도 POST 요청인 경우 특정 유저에게 질문할 수 있다.")
  public void ask_without_login_success() throws Exception {

    // given
    AskReqDto askReqDto = new AskReqDto();
    askReqDto.setAnswerUserId(answerUser.getId());
    askReqDto.setContents("2L 유저에게 익명 유저가 보내는 질문?");

    Ask ask = Ask.builder()
        .askUser(this.askUser)
        .contents("asker가 answerer에게 보내는 질문")
        .askType(AskType.USER)
        .build();

    AskInfoDto askInfo = AskDto.fromEntityToAskInfo(ask);

    given(dispatchService.dispatchAsk(any(), any(AskReqDto.class))).willReturn(askInfo);

    String requestBody = objectMapper.writeValueAsString(askReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/asks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    // then
    resultActions.andExpect(status().isCreated());
    // TODO: JSON Response Test
  }

  @Test
  // setupBefore=TEST_METHOD 메서드 실행 전에 수행, beforeEach 이전
  // setupBefore=TEST_EXECUTION beforeEach 이후 실 테스트 메서드 이전에
  @WithUserDetails(value = "asker@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("질문 요청 성공 - 로그인 유저는 특정 유저에게 질문할 수 있다.")
  public void ask_with_login_user_success() throws Exception {

    // given
    AskReqDto askReqDto = new AskReqDto();
    askReqDto.setAnswerUserId(answerUser.getId());
    askReqDto.setContents("asker가 answerer에게 보내는 질문");

    Ask ask = Ask.builder()
        .askUser(this.askUser)
        .contents(askReqDto.getContents())
        .askType(AskType.USER)
        .build();

    AskInfoDto askInfo = AskDto.fromEntityToAskInfo(ask);
    given(dispatchService.dispatchAsk(any(), any(AskReqDto.class))).willReturn(askInfo);

    String requestBody = objectMapper.writeValueAsString(askReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/asks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    System.out.println(resultActions.andReturn().getResponse().getContentAsString());

    // then
    resultActions.andExpect(status().isCreated());
  }

  @Test
  @DisplayName("질문 요청 실패 - GET 메서드로 질문 관련 URL에 요청할 수 없다.")
  public void ask_get_method_failure() throws Exception {

    // given
    AskReqDto askReqDto = new AskReqDto();
    askReqDto.setAnswerUserId(2L);
    askReqDto.setContents("2L 유저에게 익명 유저가 보내는 질문?");

    String requestBody = objectMapper.writeValueAsString(askReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/asks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }
}
