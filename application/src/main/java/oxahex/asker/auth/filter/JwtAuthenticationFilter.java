package oxahex.asker.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.auth.dto.TokenDto;
import oxahex.asker.auth.token.JwtTokenProvider;
import oxahex.asker.auth.token.JwtTokenType;
import oxahex.asker.auth.dto.LoginDto.LoginReqDto;
import oxahex.asker.auth.dto.LoginDto.LoginResDto;
import oxahex.asker.error.exception.AuthException;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.RedisUtil;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private static final String LOGIN_PATH = "/api/auth/login";
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;   // 24h
  private final ObjectMapper objectMapper;
  private final RedisUtil redisUtil;
  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      ObjectMapper objectMapper,
      RedisUtil redisUtil
  ) {
    super(authenticationManager);
    setFilterProcessesUrl(LOGIN_PATH);
    this.authenticationManager = authenticationManager;
    this.objectMapper = objectMapper;
    this.redisUtil = redisUtil;
  }


  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws AuthenticationException {

    log.info("[{}] 로그인 시도", request.getRequestURI());

    try {
      LoginReqDto loginReqDto =
          objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

      log.info("request email={}, password={}", loginReqDto.getEmail(), loginReqDto.getPassword());

      // 강제 로그인 처리: 미인증된 Authentication 객체 생성
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              loginReqDto.getEmail(), loginReqDto.getPassword()
          );

      // 검증 성공 시 AuthenticationManager.authenticate 실행(loadByUsername)
      // JWT 사용하더라도 Spring Security 처리를 해주기 위함
      return authenticationManager.authenticate(authenticationToken);

    } catch (Exception e) {
      // unsuccessfulAuthentication
      // InternalAuthenticationServiceException
      throw new AuthException(AuthError.AUTHENTICATION_FAILURE);
    }
  }

  /**
   * Email, Password 인증 성공 시 JWT Token 발급
   */
  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult
  ) throws IOException, ServletException {

    log.info("JwtAuthenticationFilter.successfulAuthentication request={}",
        request.getRequestURI());

    AuthUser authUser = (AuthUser) authResult.getPrincipal();

    // Access Token
    String accessToken = JwtTokenProvider.create(authUser, JwtTokenType.ACCESS_TOKEN);

    // Refresh Token - Redis, RDB 저장
    String refreshToken = JwtTokenProvider.create(authUser, JwtTokenType.REFRESH_TOKEN);
    redisUtil.set(authUser.getUsername(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

    TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
    LoginResDto loginResDto = new LoginResDto(authUser.getUser(), tokenDto);

    ResponseUtil.success(objectMapper, response, HttpStatus.OK, "정상적으로 로그인 되었습니다.", loginResDto);
  }

  /**
   * Email, Password 인증 실패 시
   */
  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed
  ) throws IOException, ServletException {

    log.info("JwtAuthenticationFilter.unsuccessfulAuthentication request={}",
        request.getRequestURI());

    ResponseUtil.failure(
        objectMapper,
        response,
        AuthError.AUTHENTICATION_FAILURE.getHttpStatus(),
        AuthError.AUTHENTICATION_FAILURE.getErrorMessage()
    );
  }
}
