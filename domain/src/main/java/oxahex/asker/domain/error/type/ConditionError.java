package oxahex.asker.domain.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConditionError {

  INVALID_SORT_TYPE("정렬 형식이 올바르지 않습니다.");

  private final String errorMessage;
}
