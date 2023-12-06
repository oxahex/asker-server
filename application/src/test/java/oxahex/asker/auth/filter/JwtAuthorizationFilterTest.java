package oxahex.asker.auth.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.auth.token.JwtTokenProvider;
import oxahex.asker.auth.token.JwtTokenService;
import oxahex.asker.auth.token.JwtTokenType;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.mock.MockUser;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class JwtAuthorizationFilterTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() throws Exception {

  }

  @Test
  @DisplayName("인가 성공 - 유효한 Access Token으로 인가 필요한 URL 요청 시 인가 필터를 통과한다.")
  public void authorize_success() throws Exception {

    // given
    User user = mockUser(1L, "test", "1234567890");
    AuthUser authUser = new AuthUser(user);
    String accessToken = JwtTokenProvider.create(authUser, JwtTokenType.ACCESS_TOKEN);

    // when: 없는 주소
    ResultActions resultActions =
        mockMvc.perform(get("/api/users/test")
            .header("Authorization", "Bearer " + accessToken));

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("인가 실패 - Access Token 없이 인가 필요한 URL 요청 시 403을 반환한다.")
  public void authorize_failure() throws Exception {

    // given

    // when: 없는 주소로 Access Token 없이 요청
    ResultActions resultActions = mockMvc.perform(get("/api/users/test"));

    // then
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("인가 성공 - Access Token이 유효하지 않은 경우 Redis, RDB에 Refresh Token이 유효하면 정상적으로 인가 처리 된다.")
  public void authorize_success_refresh_token() throws Exception {

    // given
    // 기존 유저 생성
    User user = newUser("test", "1234567890");
    userRepository.save(user);

    // 인가 유저 생성
    AuthUser authUser = new AuthUser(user);
    String accessToken = JwtTokenProvider.create(authUser, JwtTokenType.TEST_TOKEN);
    String refreshToken = JwtTokenProvider.create(authUser, JwtTokenType.REFRESH_TOKEN);

    Thread.sleep(1000); // TEST TOKEN 유효시간 만료를 위한 코드 TODO: 더 좋은 방법이 있을지?

    // Redis, RDB Refresh Token 저
    user.setRefreshToken(refreshToken);
    jwtTokenService.setRefreshToken(user.getEmail(), refreshToken);

    // when: 없는 주소, 만료된 토큰
    ResultActions resultActions =
        mockMvc.perform(get("/api/users/test")
            .header("Authorization", "Bearer " + accessToken));

    // then
    resultActions.andExpect(status().isNotFound());
  }
}