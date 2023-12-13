package oxahex.asker.domain.answer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.user.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerDomainService {

  private final AnswerRepository answerRepository;

  /**
   * 답변 생성
   *
   * @param dispatch 질문 전송 내역
   * @param contents 답변 내용
   * @return 생성된 답변
   */
  @Transactional
  public Answer createAnswer(Dispatch dispatch, String contents) {

    return Answer.builder()
        .answerUser(dispatch.getAnswerUser())
        .ask(dispatch.getAsk())
        .contents(contents)
        .build();
  }

  // 답변 수정

  // 답변 삭제

  /**
   * 답변 목록 조회
   *
   * @param answerUser 답변한 유저
   * @return 해당 유저가 답변한 모든 질문 목록
   */
  public List<Answer> findAnswers(User answerUser, SortType sortType) {

    if (sortType == SortType.DESC) {
      return answerRepository.findAllByAnswerUserDesc(answerUser);
    }
    return answerRepository.findAllByAnswerUserASC(answerUser);
  }
}
