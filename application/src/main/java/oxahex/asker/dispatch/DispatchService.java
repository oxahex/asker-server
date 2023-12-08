package oxahex.asker.dispatch;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
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
  private final AnswerDomainService answerDomainService;
  private final DispatchDomainService dispatchDomainService;

  @Transactional
  public Ask dispatchAsk(AuthUser authUser, AskReqDto askReqDto) {

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
    dispatchDomainService.createAskDispatch(askReqDto.getAnswerUserId(), ask);

    return ask;
  }

  @Transactional
  public Answer dispatchAnswer(User answerUser, AnswerReqDto answerReqDto) {

    log.info("[DispatchService][답변하기] dispatch_id={}, contents={}",
        answerReqDto.getDispatchId(), answerReqDto.getContents());

    Dispatch dispatch = dispatchDomainService.findDispatch(answerReqDto.getDispatchId());

    // 전송 내역에 명시된 답변 가능 유저와 요청 유저가 다른 경우
    if (!Objects.equals(dispatch.getAnswerUser().getId(), answerUser.getId())) {
      // 에러 던짐
    }

    // 문제 없으면 답변 생성
    return answerDomainService.createAnswer(dispatch, answerReqDto.getContents());
  }


}
