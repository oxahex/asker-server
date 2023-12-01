package oxahex.asker.auth.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.auth.token.JwtTokenProvider;
import oxahex.asker.auth.token.JwtTokenType;
import oxahex.asker.domain.user.User;
import oxahex.asker.mock.MockUser;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class JwtAuthorizationFilterTest extends MockUser {

  @Autowired
  private MockMvc mockMvc;

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
}