package oxahex.asker.dto.user.dto;

import lombok.Getter;
import lombok.Setter;
import oxahex.asker.domain.user.User;

public class UserDto {

  @Getter
  @Setter
  public static class UserInfoDto {

    private Long id;
    private String name;
    private String email;
  }

  public static UserInfoDto fromEntityToUserInfo(User user) {
    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setId(user.getId());
    userInfo.setName(user.getName());
    userInfo.setEmail(user.getEmail());

    return userInfo;
  }
}
