package oxahex.asker.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String JWT_HEADER_NAME = "Authorization";
  private static final String JWT_HEADER_PREFIX = "Bearer ";


  private final ObjectMapper objectMapper;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      ObjectMapper objectMapper
  ) {
    super(authenticationManager);
    this.objectMapper = objectMapper;
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

    // Access Token Request Header에  없는 경우 인가 처리 하지 않고 다음 필터로
    if (!verifyHeader(request, response)) {
      log.info("[JwtAuthorizationFilter] 토큰이 없음");
      filterChain.doFilter(request, response);
      return;
    }

    // Access token from request header(Authorization)
    String accessToken = request.getHeader(JWT_HEADER_NAME).replace(JWT_HEADER_PREFIX, "");

    // Expired access token
    if (JwtTokenProvider.isExpiredToken(accessToken)) {

      log.info("[Expired Access Token]");
      ResponseUtil.failure(
          objectMapper,
          response,
          AuthError.EXPIRED_ACCESS_TOKEN.getHttpStatus(),
          AuthError.EXPIRED_ACCESS_TOKEN.getErrorMessage()
      );

      // 다음 필터를 타지 않음
      return;
    }

    AuthUser authUser = JwtTokenProvider.verify(accessToken);

    // 생성된 AuthUser 객체로 인증된 Authentication 객체 생성
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
