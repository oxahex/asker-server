package oxahex.asker.domain.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.error.exception.NotificationException;
import oxahex.asker.domain.error.type.NotificationError;
import oxahex.asker.domain.user.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationDomainService {

  private final NotificationRepository notificationRepository;

  @Transactional
  public void createNotification(
      User receiveUser,
      Ask ask
  ) {

    log.info("[질문 생성 시 알림 저장]");

    Notification notification = Notification.builder()
        .receiveUser(receiveUser)
        .notificationType(NotificationType.ASK)
        .originId(ask.getId())
        .originUserId(ask.getAskUser() == null ? null : ask.getAskUser().getId())
        .excerpt(ask.getContents().substring(0, 10).trim())
        .build();

    notificationRepository.save(notification);
  }

  @Transactional
  public void createNotification(
      User receiveUser,
      Answer answer
  ) {

    log.info("[답변 생성 시 알림 저장]");

    Notification notification = Notification.builder()
        .receiveUser(receiveUser)
        .notificationType(NotificationType.ANSWER)
        .originId(answer.getId())
        .originUserId(answer.getAnswerUser().getId())
        .excerpt(answer.getContents().substring(0, 10).trim())
        .build();

    notificationRepository.save(notification);
  }

  public Slice<Notification> findNotifications(User user, PageRequest pageRequest) {

    return notificationRepository.findAllUnReadNotification(user, pageRequest);
  }

  /**
   * 알림 상세 조회
   *
   * @param notificationId 조회하려는 알림 ID
   * @return 해당 알림 데이터
   */
  public Notification findNotification(Long notificationId) {

    return notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NotificationError.NOTIFICATION_NOT_FOUND));
  }
}
