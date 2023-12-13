package oxahex.asker.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;
import oxahex.asker.dto.user.dto.UserDto;
import oxahex.asker.dto.user.dto.UserDto.UserAnswersDto;
import oxahex.asker.dto.user.dto.UserDto.UserAsksDto;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final UserDomainService userDomainService;
  private final DispatchDomainService dispatchDomainService;
  private final AnswerDomainService answerDomainService;


  /**
   * 특정 유저의 답변 목록 조회
   *
   * @param userId 유저 ID
   * @return 답변 목록
   */
  public UserAnswersDto getUserAnswers(Long userId) {

    User user = userDomainService.findUser(userId);
    List<Answer> answers = answerDomainService.findAnswers(user.getId());

    return UserDto.fromEntityToUserAnswers(user, answers);
  }
}
