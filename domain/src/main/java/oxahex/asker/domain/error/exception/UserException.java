package oxahex.asker.domain.error.exception;

import lombok.Getter;
import oxahex.asker.domain.error.type.UserError;

@Getter
public class UserException extends RuntimeException {

  int statusCode;
  String errorMessage;

  public UserException(UserError userError) {
    this.statusCode = userError.getStatusCode();
    this.errorMessage = userError.getErrorMessage();
  }
}
