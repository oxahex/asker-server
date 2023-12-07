package oxahex.asker.auth.token;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.user.UserService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtTokenService {

  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;   // 24h
  private final RedisTemplate<String, String> redisTemplate;
  private final UserService userService;

  @Transactional
  public void setRefreshToken(String email, String refreshToken) {
    set(email, refreshToken);
    userService.updateRefreshToken(email, refreshToken);
  }

  /**
   * Redis, RDB에서 Refresh Token을 찾아 반환
   *
   * @param email Redis, RDB Refresh Token을 찾을 유저의 Email
   * @return Refresh Token, 없는 경우 null
   */
  public String getRefreshToken(String email) {

    // Redis에서 Refresh Token 찾음
    String refreshToken = get(email);
    log.info("[Redis Refresh Token] Redis에서 찾음");

    // 없으면 RDB에서 찾고, Redis 캐싱
    if (refreshToken == null) {
      refreshToken = userService.getRefreshToken(email);
      set(email, refreshToken);
      log.info("[RDB Refresh Token] RDB에서 찾음");
    }

    return refreshToken;
  }

  private void set(String key, String value) {
    // 기존에 있으면 제거
    if (get(key) != null) {
      delete(value);
    }

    log.info("[JwtTokenService.set] key={}, token={}", key, value);
    redisTemplate.opsForValue().set(
        key, value, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS
    );
  }

  private String get(String key) {

    // Token이 존재하면 해당 Token 반환
    if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
      return redisTemplate.opsForValue().get(key);
    } else {
      return null;
    }
  }

  private boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

}
