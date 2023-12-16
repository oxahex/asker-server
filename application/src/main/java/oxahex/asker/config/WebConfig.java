package oxahex.asker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import oxahex.asker.domain.condition.converter.SortTypeConverter;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new SortTypeConverter());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {

    log.debug("[PasswordEncoder] Bean 등록");
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ObjectMapper objectMapper() {

    log.debug("[ObjectMapper] Bean 등록");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

}

