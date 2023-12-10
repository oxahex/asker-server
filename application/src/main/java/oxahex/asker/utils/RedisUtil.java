package oxahex.asker.utils;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisUtil {

  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public void set(String key, String value, long ttl) {
    // 기존에 있으면 제거
    if (get(key) != null) {
      delete(value);
    }

    redisTemplate.opsForValue().set(
        key, value, ttl, TimeUnit.MILLISECONDS
    );
  }

  public String get(String key) {

    // key에 해당하는 값이 존재하면 해당 값 반환
    if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
      return redisTemplate.opsForValue().get(key);
    } else {
      return null;
    }
  }

  @Transactional
  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }
}
