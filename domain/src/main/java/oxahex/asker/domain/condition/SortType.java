package oxahex.asker.domain.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@Getter
@RequiredArgsConstructor
public enum SortType {

  ASC("asc", Direction.ASC),
  DESC("desc", Direction.DESC);

  private final String condition;
  private final Direction direction;
}

