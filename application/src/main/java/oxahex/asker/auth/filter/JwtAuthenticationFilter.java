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
import oxahex.asker.auth.jwt.JwtTokenProvider;
import oxahex.asker.auth.jwt.TokenType;
import oxahex.asker.dto.user.UserReqDto.LoginReqDto;
import oxahex.asker.dto.user.UserResDto.LoginResDto;
import oxahex.asker.error.exception.AuthException;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private static final String HEADER = "Authorization";
  private static final String LOGIN_PATH = "/auth/login";

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager
  ) {
    super(authenticationManager);
    super.setFilterProcessesUrl(LOGIN_PATH);
  }


  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws AuthenticationException {

    log.info("JwtAuthenticationFilter.attemptAuthentication request={}", request.getRequestURI());

    ObjectMapper objectMapper = new ObjectMapper();

    try {
      LoginReqDto loginReqDto =
          objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

      // 강제 로그인 처리: 미인증된 Authentication 객체 생성
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              loginReqDto.getEmail(), loginReqDto.getPassword()
          );

      // 검증 성공 시 AuthenticationManager.authenticate 실행(loadByUsername)
      // JWT 사용하더라도 Spring Security 처리를 해주기 위함
      return super.getAuthenticationManager().authenticate(authenticationToken);

    } catch (Exception e) {
      // unsuccessfulAuthentication
      // InternalAuthenticationServiceException
      throw new AuthException(AuthError.AUTHENTICATION_FAILURE);
    }
  }

  /**
   * Email, Password 인증 성공 시 JWT Token 발급
   * <ol>
   *     <li>JWT Access Token 발급</li>
   *     <li>JWT Refresh Token 발급</li>
   *     <li>Response Header에 Access Token 전송</li>
   *     <li>Refresh Token은 Redis에 저장, 응답 Header로 전송하지 않음</li>
   * </ol>
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
    String accessToken = JwtTokenProvider.create(authUser, TokenType.ACCESS_TOKEN);
    // TODO: Refresh Token 처리

    response.addHeader(HEADER, accessToken);

    LoginResDto loginResDto = new LoginResDto(authUser.getUser());
    ResponseUtil.success(response, HttpStatus.OK, "정상적으로 로그인 되었습니다.", loginResDto);
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
        response,
        AuthError.AUTHENTICATION_FAILURE.getHttpStatus(),
        AuthError.AUTHENTICATION_FAILURE.getErrorMessage()
    );
  }
}
