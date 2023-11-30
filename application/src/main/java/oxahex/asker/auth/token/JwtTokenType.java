package oxahex.asker.auth.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenType {

  ACCESS_TOKEN(1000 * 60 * 60),               // 1 hour
  REFRESH_TOKEN(1000 * 60 * 60 * 24),         // 24 hour
  TEST_TOKEN(1000 * 60);                      // 1 min(for test)

  private final long expireTime;
}
