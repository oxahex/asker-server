package oxahex.asker.domain.ask;

import jakarta.persistence.Column;
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
@Table(name = "ask")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Ask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ask_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ask_user_id")
  private User askUser;

  @Column(nullable = false, length = 800)
  private String contents;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private AskType askType;

  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Builder
  public Ask(
      Long id,
      User askUser,
      String contents,
      AskType askType
  ) {

    this.id = id;
    this.askUser = askUser;
    this.contents = contents;
    this.askType = askType;
  }
}
