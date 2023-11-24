package oxahex.asker.domain.notification;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import oxahex.asker.domain.user.User;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receive_user_id")
  private User receiveUser;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private NotificationType type;

  @Convert(converter = NotificationAttributeConverter.class)
  private NotificationAttribute attribute;

  @Column(name = "read_date")
  private LocalDateTime readDate;

  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Builder
  public Notification(
      Long id,
      User receiveUser,
      NotificationType type,
      NotificationAttribute attribute,
      LocalDateTime readDate,
      LocalDateTime createdDate
  ) {
    this.id = id;
    this.receiveUser = receiveUser;
    this.type = type;
    this.attribute = attribute;
    this.readDate = readDate;
    this.createdDate = createdDate;
  }
}
