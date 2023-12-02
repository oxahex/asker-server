package oxahex.asker.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * Email, Password 로그인 실패 시 예외 처리
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) {

    log.error("[AuthenticationExceptionHandler] 로그인 실패");
    ResponseUtil.failure(
        objectMapper,
        response,
        AuthError.AUTHENTICATION_FAILURE.getHttpStatus(),
        AuthError.AUTHENTICATION_FAILURE.getErrorMessage()
    );
  }
}
