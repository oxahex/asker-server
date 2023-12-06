package oxahex.asker.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.error.exception.UserException;
import oxahex.asker.domain.error.type.UserError;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDomainService {

  private final UserRepository userRepository;

  /**
   * 유저 조회
   *
   * @param email 조회하려는 유저의 이메일
   * @return 해당 이메일로 가입한 유저 객체
   * @throws UserException 해당 email을 사용하는 유저가 없는 경우
   */
  public User findUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(UserError.NOT_FOUND_USER));
  }

  /**
   * 유저 생성
   *
   * @param name            가입 유저 이름
   * @param email           가입 유저 이메일
   * @param encodedPassword 암호화 된 패스워드
   * @return 생성된 유저 객체
   */
  @Transactional
  public User createUser(String name, String email, String encodedPassword) {

    // 유저 생성 시 검증
    validateCreateUser(email);

    return userRepository.save(User.builder()
        .name(name)
        .email(email)
        .password(encodedPassword)
        .role(RoleType.USER)
        .build());
  }

  /**
   * 유저 정보 수정
   *
   * @return 수정된 유저 객체
   */
  public User updateUser() {
    return null;
  }

  /**
   * 유저 삭제
   *
   * @return 삭제한 유저 객체
   */
  public User deleteUser() {

    return null;
  }

  /**
   * 특정 유저의 Refresh Token 반환
   *
   * @param email 찾고자 하는 유저 Email
   * @return 해당 유저의 JWT Refresh Token
   */
  public String getRefreshToken(String email) {
    User user = findUser(email);

    return user.getJwtToken();
  }

  @Transactional
  public void updateRefreshToken(String email, String refreshToken) {
    User user = findUser(email);
    user.setRefreshToken(refreshToken);

    userRepository.save(user);
  }


  /**
   * 유저 생성 검증
   * <p>이메일 중복 불가
   *
   * @param email 검증할 이메일
   * @throws UserException 이미 등록된 email 인 경우
   */
  private void validateCreateUser(String email) {

    if (userRepository.existsByEmail(email)) {
      throw new UserException(UserError.ALREADY_EXIST_EMAIL);
    }
  }
}
