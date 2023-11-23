package oxahex.asker.domain.answer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.user.User;

@Entity
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Answer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "answer_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "answer_user_id")
  private User answerUser;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ask_id")
  private Ask ask;

  @Column(nullable = false, length = 800)
  private String contents;

  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Builder
  public Answer(
      Long id,
      User answerUser,
      Ask ask,
      String contents
  ) {

    this.id = id;
    this.answerUser = answerUser;
    this.ask = ask;
    this.contents = contents;
  }
}
