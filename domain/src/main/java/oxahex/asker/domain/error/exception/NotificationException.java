package oxahex.asker.domain.error.exception;

import oxahex.asker.domain.error.type.NotificationError;

public class NotificationException extends RuntimeException {
  
  int statusCode;
  String errorMessage;

  public NotificationException(NotificationError notificationError) {
    this.statusCode = notificationError.getStatusCode();
    this.errorMessage = notificationError.getErrorMessage();
  }
}
