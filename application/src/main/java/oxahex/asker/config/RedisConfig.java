package oxahex.asker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;
  @Value("${spring.data.redis.port}")
  private String port;

  // Redis Connection Pool
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
    conf.setHostName(this.host);
    conf.setPort(Integer.parseInt(this.port));

    return new LettuceConnectionFactory(conf);
  }

  // Redis Template으로 Redis 접근
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    return redisTemplate;
  }
}

