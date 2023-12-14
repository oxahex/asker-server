package oxahex.asker.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.PostedAnswersDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.answer.AnswerDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerService {

  private final UserDomainService userDomainService;
  private final AnswerDomainService answerDomainService;

  /**
   * 특정 유저의 답변 조회
   *
   * @param answerUserId 답변한 유저 ID
   * @return 답변 목록
   */
  public PostedAnswersDto getAnswers(Long answerUserId, PageRequest pageRequest) {

    User answerUser = userDomainService.findUser(answerUserId);

    // Answer
    Page<Answer> answers = answerDomainService.findAnswers(answerUser, pageRequest);

    // 질문 전송 내역에서 질문 추출, 없으면 null 반환
    return AnswerDto.fromEntityToPostedAnsweredDto(answerUser, answers);
  }
}
