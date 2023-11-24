package oxahex.asker.error.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

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
        response,
        AuthError.AUTHENTICATION_FAILURE.getHttpStatus(),
        AuthError.AUTHENTICATION_FAILURE.getErrorMessage()
    );
  }
}
