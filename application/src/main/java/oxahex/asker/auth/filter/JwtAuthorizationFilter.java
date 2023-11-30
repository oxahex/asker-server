package oxahex.asker.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private static final String[] EXCLUDE_PATH = {
      "/api/auth/join", "/api/auth/login", "/api/auth/logout", "/api/asks"
  };

  /**
   * JWT 유효성 검사 예외 URL 지정
   */
  @Override
  protected boolean shouldNotFilter(
      HttpServletRequest request
  ) throws ServletException {

    String path = request.getRequestURI();

    return Arrays.stream(EXCLUDE_PATH).anyMatch(path::startsWith);
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

    log.info("JwtFilter.doFilterInternal request={}", request.getRequestURI());

    filterChain.doFilter(request, response);
  }
}
