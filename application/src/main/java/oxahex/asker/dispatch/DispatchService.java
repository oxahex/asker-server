package oxahex.asker.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskDomainService;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DispatchService {

  private final UserDomainService userDomainService;
  private final AskDomainService askDomainService;
  private final DispatchDomainService dispatchDomainService;

  @Transactional
  public Dispatch dispatch(AuthUser authUser, AskReqDto askReqDto) {

    log.info("[DispatchService][질문하기] ask_user_id={}, contents={}",
        askReqDto.getAnswerUserId(), askReqDto.getContents());

    // 익명 질문인 경우 null
    User askUser = null;
    if (authUser != null) {
      askUser = userDomainService.findUser(authUser.getUser().getId());
    }

    // 질문 생성
    Ask ask = askDomainService.createAsk(askUser, askReqDto.getContents());

    // 디스패치 생성
    return dispatchDomainService.createAskDispatch(askReqDto.getAnswerUserId(), ask);
  }
}
