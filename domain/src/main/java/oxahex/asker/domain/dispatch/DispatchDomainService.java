package oxahex.asker.domain.dispatch;

import java.util.List;
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


  /**
   * 답변 가능한 질문 조회
   *
   * @param answerUserId 유저 아이디
   * @return 해당 유저에게 온 질문 전송 내역 목록
   */
  public List<Dispatch> findDispatches(Long answerUserId) {
    // TODO: 질문 타입이 Dispatch에 있어야 할 것 같은데
    // TODO: 정렬은 어떻게?
    // Dispatch에 질문 시각을 넣어서 그 순서대로 DB에서부터 가져오게 할지
    // 아니면 Dispatch에서 Ask를 가져온 다음 sorting 하는 것이 나을지?
    // 어떻게 판단?

    return dispatchRepository.findAllByAnswerUser_Id(answerUserId);
  }
}
