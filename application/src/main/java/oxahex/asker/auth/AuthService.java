package oxahex.asker.auth;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.dto.TokenDto;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;
import oxahex.asker.auth.dto.JoinDto.JoinReqDto;
import oxahex.asker.auth.dto.JoinDto.JoinResDto;
import oxahex.asker.error.exception.AuthException;
import oxahex.asker.error.type.AuthError;
import oxahex.asker.utils.RedisUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;   // 24h

  private final RedisUtil redisUtil;
  private final PasswordEncoder passwordEncoder;
  private final UserDomainService userDomainService;

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    log.info("AuthService.loadUserByUsername={}", username);
    User user = userDomainService.findUser(username);
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

    return new JoinResDto(userDomainService.createUser(
        joinReqDto.getName(),
        joinReqDto.getEmail(),
        this.passwordEncoder.encode(joinReqDto.getPassword())
    ));
  }

  /**
   * Access Token 재발급
   *
   * @param authUser     Refresh Token으로 로그인한 유저
   * @param refreshToken 유저의 Refresh Token
   * @return Access Token, Refresh Token
   */
  @Transactional
  public TokenDto refreshAccessToken(AuthUser authUser, String refreshToken) {

    String email = authUser.getUsername();
    String cachedToken = redisUtil.get(email);

    // 캐시 되지 않은 경우 RDB에서 Refresh Token 획득
    String savedToken = cachedToken == null
        ? getTokenFromRdb(email)
        : cachedToken;

    // 서버 측에 저장된 토큰과 동일한 토큰인지 검증
    validateRefreshToken(refreshToken, savedToken);

    return new TokenDto(savedToken, refreshToken);
  }

  private String getTokenFromRdb(String email) {

    log.info("[Refresh Token] 캐싱된 토큰 없음");
    String savedToken = userDomainService.getRefreshToken(email);
    redisUtil.set(email, savedToken, REFRESH_TOKEN_EXPIRE_TIME);

    return savedToken;
  }

  private void validateRefreshToken(String savedToken, String refreshToken) {
    if (!Objects.equals(savedToken, refreshToken)) {
      throw new AuthException(AuthError.EXPIRED_ACCESS_TOKEN);
    }
  }
}
