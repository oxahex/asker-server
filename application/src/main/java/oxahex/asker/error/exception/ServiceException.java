package oxahex.asker.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import oxahex.asker.error.type.ServiceError;

@Getter
public class ServiceException extends RuntimeException {

  HttpStatus httpStatus;
  String errorMessage;

  public ServiceException(ServiceError serviceError) {
    this.httpStatus = serviceError.getHttpStatus();
    this.errorMessage = serviceError.getErrorMessage();
  }
}
