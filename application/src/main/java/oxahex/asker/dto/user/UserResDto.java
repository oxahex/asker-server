package oxahex.asker.dto.user;

import lombok.Getter;
import lombok.Setter;
import oxahex.asker.domain.user.RoleType;
import oxahex.asker.domain.user.User;

public class UserResDto {

  @Getter
  @Setter
  public static class JoinResDto {

    // TODO: OAuth Type 등 추가 정보 필요

    private String name;
    private String email;
    private RoleType role;

    public JoinResDto(User user) {
      this.name = user.getName();
      this.email = user.getEmail();
      this.role = user.getRole();
    }
  }

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
