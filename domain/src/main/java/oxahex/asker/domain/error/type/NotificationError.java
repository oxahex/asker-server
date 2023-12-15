package oxahex.asker.domain.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationError {

  NOTIFICATION_NOT_FOUND(404, "존재하지 않는 알림입니다.");

  private final int statusCode;
  private final String errorMessage;
}
