package oxahex.asker.error.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.ResponseUtil;

@Slf4j
@Component
public class AuthorizationExceptionHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws IOException, ServletException {

    log.error("[AuthorizationExceptionHandler] 권한 오류");
    ResponseUtil.failure(
        response,
        AuthError.ACCESS_DENIED.getHttpStatus(),
        AuthError.ACCESS_DENIED.getErrorMessage()
    );
  }
}
