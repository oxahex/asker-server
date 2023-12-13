package oxahex.asker.user;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchRepository;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.dto.user.dto.UserDto;
import oxahex.asker.dto.user.dto.UserDto.UserAnswersDto;
import oxahex.asker.dto.user.dto.UserDto.UserAsksDto;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class UserControllerTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DispatchRepository dispatchRepository;

  @MockBean
  private UserService userService;

  private User answerUser;

  @BeforeEach
  public void setup() {
    // 유저
    this.answerUser = userRepository.save(newUser("answerer", "1234567890"));
  }


  @Test
  @DisplayName("답변 내역 조회 API 접근 성공 - 비로그인 유저의 경우 답변 내역 조회 API에 접근할 수 있다.")
  public void getUserAnswers_success_without_login() throws Exception {

    // given: 작성한 답변 없음
    given(userService.getUserAnswers(anyLong()))
        .willReturn(new UserAnswersDto());

    // when
    String url = "/api/users/" + answerUser.getId() + "/answers";
    ResultActions resultActions = mockMvc.perform(get(url)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "answerer@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @DisplayName("답변 내역 조회 API 접근 성공 - 로그인 유저의 경우 답변 내역 조회 API에 접근할 수 있다.")
  public void getUserAnswers_success_with_login_user() throws Exception {

    // given: 작성한 답변 없음
    given(userService.getUserAnswers(anyLong()))
        .willReturn(new UserAnswersDto());

    // when
    String url = "/api/users/" + answerUser.getId() + "/answers";
    ResultActions resultActions = mockMvc.perform(get(url)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("답변 내역 조회 시 작성한 답변이 없는 경우 Empty List를 반환한다.")
  public void getUserAnswers_success_if_null() throws Exception {

    // given: 받은 질문 없음
    UserAnswersDto userAnswersDto = UserDto.fromEntityToUserAnswers(answerUser,
        new ArrayList<Answer>());
    given(userService.getUserAnswers(anyLong()))
        .willReturn(userAnswersDto);

    // when
    String url = "/api/users/" + answerUser.getId() + "/answers";
    ResultActions resultActions = mockMvc.perform(get(url)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.id").value(answerUser.getId()));
    resultActions.andExpect(jsonPath("$.data.name").value(answerUser.getName()));
    // 질문 목록은 Empty List
    resultActions.andExpect(jsonPath("$.data.answers").isArray());
    resultActions.andExpect(jsonPath("$.data.answers").isEmpty());
  }

}
