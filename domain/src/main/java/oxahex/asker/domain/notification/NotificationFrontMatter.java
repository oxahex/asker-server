package oxahex.asker.domain.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationFrontMatter {

  private Long originId;            // 알림 발생 ID(ASK_USER_ID, ANSWER_USER_ID)
  private Long originUserId;        // 알림 발생 주체 ID (ASK_ID, ANSWER_ID)
  private String excerpt;           // ASK or ANSWER CONTENTS 요약

  @Builder
  public NotificationFrontMatter(
      Long originId,
      Long originUserId,
      String excerpt
  ) {

    this.originId = originId;
    this.originUserId = originUserId;
    this.excerpt = excerpt;
  }
}

