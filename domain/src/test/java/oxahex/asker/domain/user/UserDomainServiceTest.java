package oxahex.asker.domain.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oxahex.asker.domain.error.exception.UserException;
import oxahex.asker.domain.error.type.UserError;
import oxahex.asker.domain.mock.MockObject;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest extends MockObject {

  @InjectMocks
  private UserDomainService userDomainService;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("유저 생성 - 성공")
  public void createUser_success() throws Exception {

    // given
    String name = "test";
    String email = "test@gmail.com";
    String password = "1234567890";

    // stub 1: 해당 Email로 가입한 유저 없음
    given(userRepository.existsByEmail(anyString())).willReturn(false);

    // stub 2: 해당 유저 데이터 DB 저장 성공
    User user = mockUser(1L, "test");
    given(userRepository.save(any(User.class))).willReturn(user);

    // when
    User createdUser = userDomainService.createUser(name, email, password);

    // then: name, email, password, role
    Assertions.assertEquals(name, createdUser.getName());
    Assertions.assertEquals(email, createdUser.getEmail());
    Assertions.assertEquals(password, createdUser.getPassword());
    Assertions.assertEquals(RoleType.USER, createdUser.getRole());
  }

  @Test
  @DisplayName("유저 생성 - 실패: 이미 존재하는 Email로 유저 생성 불가")
  public void createUser_failure() throws Exception {

    // given
    String name = "test";
    String email = "test@gmail.com";
    String password = "1234567890";

    // stub 1: 해당 Email로 가입한 유저 있음
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    // when
    UserException exception = assertThrows(UserException.class,
        () -> userDomainService.createUser(name, email, password));

    // then
    Assertions.assertEquals(UserError.ALREADY_EXIST_EMAIL.getStatusCode(),
        exception.getStatusCode());
    Assertions.assertEquals(UserError.ALREADY_EXIST_EMAIL.getErrorMessage(),
        exception.getErrorMessage());
  }
}
