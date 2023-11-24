package oxahex.asker.dto.user;

import lombok.Getter;
import lombok.Setter;
import oxahex.asker.domain.user.RoleType;
import oxahex.asker.domain.user.User;

public class UserResDto {

  @Getter
  @Setter
  public static class LoginResDto {

    private String name;
    private String email;
    private RoleType role;

    public LoginResDto(User user) {
      this.name = user.getName();
      this.email = user.getEmail();
      this.role = user.getRole();
    }
  }
}
