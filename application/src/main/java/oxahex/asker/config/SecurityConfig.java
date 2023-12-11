package oxahex.asker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import oxahex.asker.auth.AuthService;
import oxahex.asker.auth.filter.JwtAuthenticationFilter;
import oxahex.asker.auth.filter.JwtAuthorizationFilter;
import oxahex.asker.domain.user.RoleType;
import oxahex.asker.error.handler.AuthorizationExceptionHandler;
import oxahex.asker.error.handler.AuthenticationExceptionHandler;
import oxahex.asker.utils.RedisUtil;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;
  private final RedisUtil redisUtil;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    log.debug("[SecurityFilterChain] Bean 등록");

    http
        .headers(HeadersConfigurer::disable) // iframe 비허용 처리
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS
        .httpBasic(AbstractHttpConfigurer::disable)   // 브라우저 팝업 인증 비허용
        .formLogin(AbstractHttpConfigurer::disable)   // HTTP API 기반 서버이므로 Form Login X
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));  // JWT 기반 인가 사용

    http
        .authorizeHttpRequests(request -> {

          request.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll(); // 회원가입, 로그인, 로그아웃
          request.requestMatchers(HttpMethod.POST, "/api/asks").permitAll();  // 특정 유저에게 질문
          request.requestMatchers(HttpMethod.GET, "/api/users/**/answers/**")
              .permitAll(); // 특정 유저의 답변 조회

          request.requestMatchers("/api/users/**")
              .hasAuthority(RoleType.USER.name()); // 로그인이 필요한 요청
          request.requestMatchers("/api/admin/**")
              .hasAuthority(RoleType.ADMIN.name()); // 회원 개인 정보 관련

          request.anyRequest().authenticated();
        });

    http.apply(new CustomSecurityFilterManager());

    http
        .exceptionHandling(exceptionHandler -> {
          exceptionHandler.authenticationEntryPoint(
              new AuthenticationExceptionHandler(objectMapper)); // 인증 실패(401)
          exceptionHandler.accessDeniedHandler(
              new AuthorizationExceptionHandler(objectMapper)); // 인가(권한) 오류(403)
        });

    return http.build();

  }

  public CorsConfigurationSource corsConfigurationSource() {

    log.debug("[CorsConfigurationSource] Security Filter Chain 등록");

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");  // GET, POST, PUT, DELETE JavaScript 요청 모두 허용
    configuration.addAllowedOriginPattern("*"); // 모든 주소 허용 -> 이후 변경(프론트쪽 Origin만 허용하도록)
    configuration.setAllowCredentials(true);  // 클라이언트 쿠키 요청 허용

    // 모든 주소 요청에 위 설정 적용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("**", configuration);

    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager() {

    log.debug("[AuthenticationManager] Security Filter Chain 등록");

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(authService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }

  public class CustomSecurityFilterManager extends
      AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      AuthenticationManager authenticationManager = http.getSharedObject(
          AuthenticationManager.class);
      http.addFilter(
          new JwtAuthenticationFilter(authenticationManager, objectMapper, redisUtil));
      http.addFilter(new JwtAuthorizationFilter(authenticationManager, objectMapper));
      super.configure(http);
    }
  }
}
