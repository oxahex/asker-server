package oxahex.asker.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserService;
import oxahex.asker.dto.user.UserReqDto.JoinReqDto;
import oxahex.asker.dto.user.UserResDto.JoinResDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    log.info("AuthService.loadUserByUsername={}", username);
    User user = userService.findUser(username);
    return new AuthUser(user);
  }

  /**
   * 유저 생성
   * <p> 사용자 Name, Email, Password 를 받아 새로운 유저 생성
   * <p> 검증: Email 중복 여부
   *
   * @param joinReqDto name, email, password
   * @return 생성된 User Entity
   */
  public JoinResDto join(JoinReqDto joinReqDto) {

    log.info("[join] email={}, password={}", joinReqDto.getEmail(),
        joinReqDto.getPassword());

    return new JoinResDto(userService.createUser(
        joinReqDto.getName(),
        joinReqDto.getEmail(),
        this.passwordEncoder.encode(joinReqDto.getPassword())
    ));
  }
}
