package oxahex.asker.domain.error.exception;

import lombok.Getter;
import oxahex.asker.domain.error.type.ConditionError;

@Getter
public class ConditionException extends RuntimeException {

  final int statusCode = 400;
  String errorMessage;

  public ConditionException(ConditionError conditionError) {
    this.errorMessage = conditionError.getErrorMessage();
  }
}
