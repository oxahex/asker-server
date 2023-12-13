package oxahex.asker.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.auth.dto.JoinDto.JoinReqDto;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class AuthControllerTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("회원 가입 성공")
  public void join_success() throws Exception {

    // given
    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setName("test");
    joinReqDto.setEmail("test@gmail.com");
    joinReqDto.setPassword("1234567890");

    String requestBody = objectMapper.writeValueAsString(joinReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/auth/join").content(requestBody)
            .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated());
  }

  @Test
  @DisplayName("회원 가입 실패 - 이미 존재하는 Email로 회원 가입을 할 수 없습니다.")
  public void join_failure() throws Exception {

    // given
    dataSetUp();  // 기존에 가입된 Email

    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setName("test");
    joinReqDto.setEmail("test@gmail.com");
    joinReqDto.setPassword("1234567890");

    String requestBody = objectMapper.writeValueAsString(joinReqDto);

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/auth/join").content(requestBody)
            .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isConflict());
  }

  private void dataSetUp() {
    userRepository.save(newUser("test", "1234567890"));
  }
}
