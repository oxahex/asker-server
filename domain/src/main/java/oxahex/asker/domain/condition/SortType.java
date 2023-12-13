package oxahex.asker.domain.condition;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oxahex.asker.domain.error.exception.ConditionException;
import oxahex.asker.domain.error.type.ConditionError;

@Getter
@RequiredArgsConstructor
public enum SortType {

  ASC("asc"),
  DESC("desc");

  private final String condition;

  /**
   * 입력 값을 Enum Type으로 변경(기본값은 DESC - 최신순)
   *
   * @param value 타입 이름
   * @return 값에 해당하는 SortType Enum
   */
  public static SortType getSortType(String value) {

    return Arrays.stream(SortType.values()).
        filter(x -> x.getCondition().equals(value)).findAny()
        .orElse(SortType.DESC);
  }
}

