package oxahex.asker.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import oxahex.asker.error.type.UserError;

@Getter
public class UserException extends RuntimeException {

  HttpStatus httpStatus;
  String errorMessage;

  public UserException(UserError userError) {
    this.httpStatus = userError.getHttpStatus();
    this.errorMessage = userError.getErrorMessage();
  }
}
