package oxahex.asker.mock;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import oxahex.asker.domain.user.RoleType;
import oxahex.asker.domain.user.User;

public class MockUser {

  /**
   * For Test: 새 유저
   *
   * @param name 테스트 유저 이름
   * @return 유저
   */
  protected User newUser(String name, String password) {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(password);

    return User.builder()
        .name(name)
        .email(name + "@gmail.com")
        .password(encodedPassword)
        .role(RoleType.USER)
        .build();
  }

  /**
   * For Test: 저장된 유저
   *
   * @param id   테스트 유저 ID
   * @param name 테스트 유저 이름
   * @return 저장된 유저
   */
  protected User mockUser(Long id, String name, String password) {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(password);

    return User.builder()
        .id(id)
        .name(name)
        .email(name + "@gmail.com")
        .password(encodedPassword)
        .role(RoleType.USER)
        .build();
  }
}
