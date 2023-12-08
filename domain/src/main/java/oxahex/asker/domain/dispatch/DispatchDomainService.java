package oxahex.asker.domain.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.error.exception.DispatchException;
import oxahex.asker.domain.error.type.DispatchError;
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

  /**
   * 특정 질문 전송 내역 조회
   *
   * @param dispatchId 전송 내역 ID
   * @return 해당 하는 전송 내역
   */
  public Dispatch findDispatch(Long dispatchId) {
    return dispatchRepository.findById(dispatchId)
        .orElseThrow(() -> new DispatchException(DispatchError.DISPATCH_NOT_FOUND));
  }
}
