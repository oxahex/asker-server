package oxahex.asker.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class WebConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {

    log.debug("[PasswordEncoder] Bean 등록");
    return new BCryptPasswordEncoder();
  }
}

