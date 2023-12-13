package oxahex.asker.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.mock.MockUser;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends MockUser {

  @InjectMocks
  UserService userService;

  @Mock
  AnswerDomainService answerDomainService;

  @Mock
  private DispatchDomainService dispatchDomainService;


  @Test
  @DisplayName("질문 내역 조회 성공 - 로그인 유저는 본인에게 온 질문을 확인할 수 있다.")
  public void getReceivedAsks_success() throws Exception {

    // given

    // when
    // then
  }

}
