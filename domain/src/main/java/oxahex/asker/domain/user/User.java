package oxahex.asker.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.dispatch.Dispatch;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(unique = true, nullable = false, length = 100)
  private String email;

  @Column(name = "password", nullable = false, length = 100)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private RoleType role;

  @Column(name = "jwt_token")
  private String jwtToken;

  @OneToMany(mappedBy = "answerUser")
  private List<Dispatch> dispatches = new ArrayList<>();

  @OneToMany(mappedBy = "askUser")
  private List<Ask> asks = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDateTime;

  @LastModifiedDate
  @Column(name = "modified_date")
  private LocalDateTime modifiedDateTime;

  @Builder
  public User(
      Long id,
      String name,
      String email,
      String password,
      RoleType role,
      String jwtToken) {

    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.jwtToken = jwtToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.jwtToken = refreshToken;
  }

  public void setDispatch(Dispatch dispatch) {
    this.dispatches.add(dispatch);
  }

  public void setAsk(Ask ask) {
    this.asks.add(ask);
  }
}
