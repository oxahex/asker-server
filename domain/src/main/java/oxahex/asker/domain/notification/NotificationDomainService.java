package oxahex.asker.domain.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.dispatch.Dispatch;
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

}
