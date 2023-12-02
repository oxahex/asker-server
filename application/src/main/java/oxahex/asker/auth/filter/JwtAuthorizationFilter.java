package oxahex.asker.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.auth.token.JwtTokenProvider;
import oxahex.asker.auth.token.JwtTokenService;
import oxahex.asker.auth.token.JwtTokenType;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String JWT_HEADER_NAME = "Authorization";
  private static final String JWT_HEADER_PREFIX = "Bearer ";
  private final JwtTokenService jwtTokenService;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
      JwtTokenService jwtTokenService) {
    super(authenticationManager);
    this.jwtTokenService = jwtTokenService;
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
    // Token이 없는 경우 인가 처리 하지 않고 다음 필터로
    if (!verifyHeader(request, response)) {
      log.info("[JwtAuthorizationFilter] 토큰이 없음");
      filterChain.doFilter(request, response);
      return;
    }

    AuthUser authUser = null;
    String token = request.getHeader(JWT_HEADER_NAME).replace(JWT_HEADER_PREFIX, "");
    log.info("[토큰이 존재함] token={}", token);

    // 만료 시간 검증
    // 만료된 토큰인 경우 Redis, RDB에서 Refresh Token을 가져와 Access Token으로 활용
    if (JwtTokenProvider.isExpiredToken(token)) {

      log.info("[JwtAuthorizationFilter] 만료된 토큰");

      // Redis, DB 순서로 찾아서 Refresh Token을 가져옴
      String email = JwtTokenProvider.getUserEmail(token);
      token = jwtTokenService.getRefreshToken(email);

      // RDB, Redis에 Refresh Token이 없거나 유효하지 않은 경우 재로그인 -> 인가 처리 하지 않고 다음 필터로
      if (token == null || JwtTokenProvider.isExpiredToken(token)) {
        log.info("[JwtAuthorizationFilter] Refresh Token 없음");
        filterChain.doFilter(request, response);
        return;
      }

      // Refresh Token으로 AuthUser 생성
      authUser = JwtTokenProvider.verify(token);

      // Header에 새 Access Token 발급.
      // Access Token이 유효하지 않고, Refresh Token이 유효한 경우에만 Access Token 재발급
      String reissuedAccessToken = JwtTokenProvider.create(authUser, JwtTokenType.ACCESS_TOKEN);
      response.addHeader(JWT_HEADER_NAME, JWT_HEADER_PREFIX + reissuedAccessToken);
    }

    // Access Token이 유요한 경우 Access Token으로 AuthUser 생성
    authUser = JwtTokenProvider.verify(token);

    // 인증된 Authentication 객체
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            authUser, null, authUser.getAuthorities()
        );

    // SecurityContextHolder 저장
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }

  private boolean verifyHeader(
      HttpServletRequest request,
      HttpServletResponse response
  ) {

    String header = request.getHeader(JWT_HEADER_NAME);

    // Header가 없거나, 시작 값이 'Bearer '가 아닌 경우
    return header != null && header.startsWith(JWT_HEADER_PREFIX);
  }
}
