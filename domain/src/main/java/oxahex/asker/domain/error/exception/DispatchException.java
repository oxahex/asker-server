package oxahex.asker.domain.error.exception;

import lombok.Getter;
import oxahex.asker.domain.error.type.DispatchError;

@Getter
public class DispatchException extends RuntimeException {

  int statusCode;
  String errorMessage;

  public DispatchException(DispatchError dispatchError) {
    this.statusCode = dispatchError.getStatusCode();
    this.errorMessage = dispatchError.getErrorMessage();
  }
}
