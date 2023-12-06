package oxahex.asker.domain.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DispatchDomainService {

  private final UserDomainService userDomainService;
  private final DispatchRepository dispatchRepository;


  // 생성
  @Transactional
  public Dispatch createAskDispatch(Long answerUserId, Ask ask) {

    User answerUser = userDomainService.findUser(answerUserId);
    return dispatchRepository.save(Dispatch.builder()
        .answerUser(answerUser)
        .ask(ask).build());
  }

  @Transactional
  public Dispatch createAnswerDispatch() {

    return null;
  }

  // 삭제
  public Dispatch deleteDispatch() {

    return null;
  }


}
