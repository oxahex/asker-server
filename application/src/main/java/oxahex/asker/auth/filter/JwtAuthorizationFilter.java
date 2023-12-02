package oxahex.asker.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.auth.token.JwtTokenProvider;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String JWT_HEADER_NAME = "Authorization";
  private static final String JWT_HEADER_PREFIX = "Bearer ";

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  /**
   * JWT Token 인증 및 유효성 검사
   * <p>JWT Token 유효한 경우 Authentication 객체 생성 후 SecurityContextHolder 에 객체 추가
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    log.info("[{}] JWT 유효성 검사", request.getRequestURI());

    // Header 유효성 검증
    if (verifyHeader(request, response)) {
      // 토큰이 존재함
      String token = request.getHeader(JWT_HEADER_NAME).replace(JWT_HEADER_PREFIX, "");

      log.info("[토큰이 존재함] {}", token);
      AuthUser authUser = JwtTokenProvider.verify(token);

      // 인증된 Authentication 객체
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              authUser, null, authUser.getAuthorities()
          );

      // SecurityContextHolder 저장
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private boolean verifyHeader(
      HttpServletRequest request,
      HttpServletResponse response
  ) {

    String header = request.getHeader(JWT_HEADER_NAME);

    // Header가 없거나, 시작 값이 'Bearer '가 아닌 경우
    if (header == null || !header.startsWith(JWT_HEADER_PREFIX)) {
      return false;
    } else {
      return true;
    }
  }
}
