package oxahex.asker.domain.condition.converter;

import org.springframework.core.convert.converter.Converter;
import oxahex.asker.domain.condition.SortType;

public class SortTypeConverter implements Converter<String, SortType> {

  @Override
  public SortType convert(String source) {
    try {
      return SortType.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
