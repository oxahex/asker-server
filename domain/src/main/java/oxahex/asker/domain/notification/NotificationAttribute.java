package oxahex.asker.domain.notification;

public class NotificationAttribute {

  private Long originId;            // 알림 발생 ID(ASK_USER_ID, ANSWER_USER_ID)
  private Long originUserId;        // 알림 발생 주체 ID (ASK_ID, ANSWER_ID)
  private String excerpt;           // ASK or ANSWER CONTENTS 요약

}

