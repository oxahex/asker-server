package oxahex.asker.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import oxahex.asker.domain.user.RoleType;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserService;
import oxahex.asker.dto.auth.JoinDto.JoinReqDto;
import oxahex.asker.dto.auth.JoinDto.JoinResDto;
import oxahex.asker.mock.MockUser;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends MockUser {

  @InjectMocks
  private AuthService authService;

  @Mock
  private UserService userService;

  @Spy
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 실제 객체

  @Test
  @DisplayName("회원가입 - 성공")
  public void join_success() throws Exception {

    // given
    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setName("test");
    joinReqDto.setEmail("test@gmail.com");
    joinReqDto.setPassword("1234567890");

    // stub 1: 유저 생성, DB 저장 완료
    User createdUser = mockUser(1L, "test", "1234567890");
    given(userService.createUser(any(), any(), any())).willReturn(createdUser);

    // when
    JoinResDto joinResDto = authService.join(joinReqDto);

    // then: name, email role
    Assertions.assertEquals(joinReqDto.getName(), joinResDto.getName());
    Assertions.assertEquals(joinReqDto.getEmail(), joinResDto.getEmail());
    Assertions.assertEquals(RoleType.USER, joinResDto.getRole());
  }
}