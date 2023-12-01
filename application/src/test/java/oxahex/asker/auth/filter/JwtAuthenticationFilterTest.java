package oxahex.asker.auth.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import oxahex.asker.dto.auth.LoginDto.LoginReqDto;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class JwtAuthenticationFilterTest extends MockUser {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() throws Exception {
    // 기존 유저 생성
    userRepository.save(newUser("test", "1234567890"));
  }

  @Test
  @DisplayName("인증 성공 - Email, Password 일치, 가입된 회원의 경우 로그인에 성공하고 Access Token이 발급된다.")
  public void authenticate_success() throws Exception {

    // given
    LoginReqDto loginResDto = new LoginReqDto();
    loginResDto.setEmail("test@gmail.com");
    loginResDto.setPassword("1234567890");

    String requestBody = objectMapper.writeValueAsString(loginResDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(get("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody));

    String accessToken = resultActions.andReturn().getResponse().getHeader("Authorization");

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.name").value("test"));
    resultActions.andExpect(jsonPath("$.data.email").value("test@gmail.com"));
    resultActions.andExpect(jsonPath("$.data.role").value("USER"));
    Assertions.assertNotNull(accessToken);
    Assertions.assertTrue(accessToken.startsWith("Bearer "));
  }

  @Test
  @DisplayName("인증 실패 - Password가 맞지 않는 경유 인증에 실패하고 401 에러가 반환된다.")
  public void authenticate_failure() throws Exception {

    // given
    LoginReqDto loginResDto = new LoginReqDto();
    loginResDto.setEmail("test@gmail.com");
    loginResDto.setPassword("wrong password");

    String requestBody = objectMapper.writeValueAsString(loginResDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(get("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }
}