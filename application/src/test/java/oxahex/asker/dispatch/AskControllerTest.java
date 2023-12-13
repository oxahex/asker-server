package oxahex.asker.dispatch;

import static org.mockito.ArgumentMatchers.any;
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
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.dispatch.dto.AskDto.ReceivedAsksDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskType;
import oxahex.asker.domain.condition.SortType;
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

  @MockBean
  AskService askService;

  private User askUser;
  private User answerUser;

  @BeforeEach
  public void setup() {
    this.askUser = userRepository.save(newUser("asker", "1234567890"));
    this.answerUser = userRepository.save(newUser("answerer", "1234567890"));
  }

  @Test
  @DisplayName("질문 요청 성공 - 로그인하지 않은 유저라도 특정 유저에게 질문할 수 있다.")
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
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("질문 내역 조회 API 접근 성공 - 로그인 유저의 경우 질문 목록 조회 API 접근이 가능하다.")
  public void getReceivedAsks_success_with_login_user() throws Exception {

    // given: 받은 질문 없음
    given(askService.getReceivedAsks(any(User.class), any(SortType.class)))
        .willReturn(new ReceivedAsksDto());

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/asks")
        .param("userId", String.valueOf(answerUser.getId()))
        .param("sort", SortType.DESC.name())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("질문 내역 조회 API 접근 실패 - 비로그인 유저의 경우 질문 목록 조회 API 접근이 불가하다.")
  public void getReceivedAsks_failure_without_login() throws Exception {

    // given: 받은 질문 없음
    given(askService.getReceivedAsks(any(User.class), any(SortType.class)))
        .willReturn(new ReceivedAsksDto());

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/asks")
        .param("userId", String.valueOf(answerUser.getId()))
        .param("sort", SortType.DESC.name())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("질문 내역 조회 실패 - 로그인 유저의 경우 본인에게 온 질문만 확인할 수 있다.")
  public void getReceivedAsk_failure_no_authority_to_access() throws Exception {

    // given
    // stub
    given(askService.getReceivedAsks(any(User.class), any(SortType.class)))
        .willReturn(new ReceivedAsksDto());

    // when: 로그인 유저와 다른 유저의 질문 목록 요청
    ResultActions resultActions = mockMvc.perform(get("/api/asks")
        .param("userId", String.valueOf(answerUser.getId() + 1))
        .param("sort", SortType.DESC.name())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("질문 내역 조회 시 받은 질문이 없는 경우 Empty List를 반환한다.")
  public void getReceivedAsks_success_if_null() throws Exception {

    // given: 받은 질문 없음
    // stub
    ReceivedAsksDto receivedAsksDto = AskDto.fromEntityToReceivedAsks(answerUser,
        new ArrayList<Ask>());
    given(askService.getReceivedAsks(any(User.class), any(SortType.class)))
        .willReturn(receivedAsksDto);

    // when
    ResultActions resultActions = mockMvc.perform(get("/api/asks")
        .param("userId", String.valueOf(answerUser.getId()))
        .param("sort", SortType.DESC.name())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.answerUser.id").value(answerUser.getId()));
    resultActions.andExpect(jsonPath("$.data.answerUser.name").value(answerUser.getName()));
    // 질문 목록은 Empty List
    resultActions.andExpect(jsonPath("$.data.asks").isArray());
    resultActions.andExpect(jsonPath("$.data.asks").isEmpty());
  }
}
