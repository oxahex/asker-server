package oxahex.asker.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.domain.notification.Notification;
import oxahex.asker.domain.notification.NotificationDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.notification.dto.NotificationDto;
import oxahex.asker.notification.dto.NotificationDto.NotificationListDto;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationDomainService notificationDomainService;

  /**
   * 읽지 않은 알림 목록 조회
   *
   * @param authUser 로그인 유저
   * @return 읽지 않은 알림 목록 DTO
   */
  public NotificationListDto getNotifications(AuthUser authUser, PageRequest pageRequest) {

    User user = authUser.getUser();

    Slice<Notification> notifications =
        notificationDomainService.findNotifications(user, pageRequest);

    return NotificationDto.fromEntityToNotificationList(user, notifications);
  }
}
