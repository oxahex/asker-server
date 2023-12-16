package oxahex.asker.dispatch;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerReqDto;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.AskReqDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskDomainService;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.type.ServiceError;
import oxahex.asker.search.AnswerDocument;
import oxahex.asker.search.SearchRepository;
import oxahex.asker.search.SearchService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DispatchService {

  private final UserDomainService userDomainService;
  private final AskDomainService askDomainService;
  private final AnswerDomainService answerDomainService;
  private final DispatchDomainService dispatchDomainService;
  private final SearchService searchService;


  @Transactional
  public AskInfoDto dispatchAsk(AuthUser authUser, AskReqDto askReqDto) {

    log.info("[DispatchService] 유저 질문");
    // 익명 질문인 경우 null
    User askUser = null;
    if (authUser != null) {
      askUser = userDomainService.findUser(authUser.getUser().getId());
    }

    // 질문 생성
    Ask ask = askDomainService.createAsk(askUser, askReqDto.getContents());

    // 디스패치 생성
    dispatchDomainService.createAskDispatch(askReqDto.getAnswerUserId(), ask);

    return AskDto.fromEntityToAskInfo(ask);
  }

  @Transactional
  public AnswerInfoDto dispatchAnswer(User answerUser, AnswerReqDto answerReqDto) {

    log.info("[DispatchService][답변하기] askId={}, contents={}",
        answerReqDto.getAskId(), answerReqDto.getContents());

    // 질문 ID로 질문 전송 내역 확인
    Ask ask = askDomainService.findAsk(answerReqDto.getAskId());
    Dispatch dispatch = dispatchDomainService.findDispatch(ask);

    // 전송 내역에 명시된 답변 가능 유저와 요청 유저가 다른 경우
    if (!Objects.equals(dispatch.getAnswerUser().getId(), answerUser.getId())) {

      throw new ServiceException(ServiceError.NO_AUTHORITY_TO_ANSWER);
    }

    // 답변 생성
    Answer answer =
        answerDomainService.createAnswer(dispatch, answerReqDto.getContents());

    // ElasticSearch 저장
    searchService.saveAnswer(ask, answer);

    return AnswerDto.fromEntityToAnswerInfo(answer);
  }
}
