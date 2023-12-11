package oxahex.asker.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final DispatchDomainService dispatchDomainService;
  private final AnswerDomainService answerDomainService;

  /**
   * 특정 유저가 받은 질문 내역 조회
   *
   * @param userId 유저 ID
   * @return 받은 질문 목록
   */
  public List<AskInfoDto> getReceivedAsks(Long userId) {

    // 받은 질문 내역 확인
    List<Dispatch> dispatches = dispatchDomainService.findDispatches(userId);

    // 질문 전송 내역에서 질문 추출, 없으면 null 반환
    return dispatches.stream()
        .map(dispatch -> AskDto.fromEntityToAskInfo(dispatch.getAsk()))
        .toList();
  }

  /**
   * 특정 유저의 답변 목록 조회
   *
   * @param userId 유저 ID
   * @return 답변 목록
   */
  public List<AnswerInfoDto> getUserAnswers(Long userId) {

    List<Answer> answers = answerDomainService.findAnswers(userId);

    return answers.stream()
        .map(AnswerDto::fromEntityToAnswerInfo)
        .toList();
  }
}
