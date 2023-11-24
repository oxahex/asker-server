package oxahex.asker.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import oxahex.asker.error.type.AuthError;

@Getter
public class AuthException extends InternalAuthenticationServiceException {

  HttpStatus httpStatus;
  String errorMessage;

  public AuthException(AuthError authError) {
    super(authError.getErrorMessage());
    this.httpStatus = authError.getHttpStatus();
    this.errorMessage = authError.getErrorMessage();
  }
}
