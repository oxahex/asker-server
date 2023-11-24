package oxahex.asker.error.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

  private final Map<String, String> errorMap;

  public ValidationException(String message, Map<String, String> errorMap) {
    super(message);
    this.errorMap = errorMap;
  }
}
