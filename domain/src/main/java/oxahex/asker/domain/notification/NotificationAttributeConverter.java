package oxahex.asker.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Converter
public class NotificationAttributeConverter implements
    AttributeConverter<NotificationAttribute, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(NotificationAttribute attribute) {
    if (ObjectUtils.isEmpty(attribute)) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public NotificationAttribute convertToEntityAttribute(String dbData) {
    if (StringUtils.hasText(dbData)) {
      try {
        return objectMapper.readValue(dbData, NotificationAttribute.class);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
