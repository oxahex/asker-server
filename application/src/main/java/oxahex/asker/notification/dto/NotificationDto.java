package oxahex.asker.notification.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import oxahex.asker.auth.dto.UserDto;
import oxahex.asker.auth.dto.UserDto.UserInfoDto;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.AskListDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.notification.Notification;
import oxahex.asker.domain.notification.NotificationFrontMatter;
import oxahex.asker.domain.notification.NotificationType;
import oxahex.asker.domain.user.User;

public class NotificationDto {

  @Getter
  @Setter
  public static class NotificationInfoDto {

    private Long id;
    private NotificationFrontMatter frontMatter;
    private NotificationType notificationType;
    private LocalDateTime readDate;
    private LocalDateTime createdDate;
  }

  public static NotificationInfoDto fromEntityToNotificationInfo(Notification notification) {

    NotificationInfoDto notificationInfo = new NotificationInfoDto();
    notificationInfo.setId(notification.getId());
    notificationInfo.setFrontMatter(notification.getFrontMatter());
    notificationInfo.setNotificationType(notification.getNotificationType());
    notificationInfo.setReadDate(notification.getReadDateTime());
    notificationInfo.setCreatedDate(notification.getCreatedDateTime());

    return notificationInfo;
  }


  @Getter
  @Setter
  public static class NotificationListDto {

    private UserInfoDto userInfo;
    private Slice<NotificationInfoDto> notifications;
  }

  public static NotificationListDto fromEntityToNotificationList(
      User user,
      Slice<Notification> notifications
  ) {

    NotificationListDto notificationList = new NotificationListDto();
    notificationList.setUserInfo(UserDto.fromEntityToUserInfo(user));
    notificationList.setNotifications(
        notifications.map(NotificationDto::fromEntityToNotificationInfo));

    return notificationList;
  }
}
