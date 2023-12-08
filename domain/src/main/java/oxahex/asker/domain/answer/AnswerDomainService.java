package oxahex.asker.domain.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.dispatch.Dispatch;

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

  // 답변 조회
}
