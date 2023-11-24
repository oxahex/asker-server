package oxahex.asker.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserRepository;
import oxahex.asker.error.exception.AuthException;
import oxahex.asker.error.type.AuthError;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("AuthService.loadUserByUsername={}", username);
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new AuthException(AuthError.AUTHENTICATION_FAILURE));

    return new AuthUser(user);
  }
}
