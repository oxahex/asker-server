package oxahex.asker.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskListDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.ask.AskDomainService;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskService {

  private final UserDomainService userDomainService;
  private final AskDomainService askDomainService;

  /**
   * 특정 유저가 받은 질문 내역 조회
   *
   * @param user 로그인 유저
   * @return 받은 질문 목록
   */
  public AskListDto getAsks(User user, PageRequest pageRequest) {

    // 받은 질문 내역 확인
    User answerUser = userDomainService.findUser(user.getId());

    // ASK
    Page<Ask> asks = askDomainService.findAsks(answerUser, pageRequest);

    // 질문 전송 내역에서 질문 추출, 없으면 null 반환
    return AskDto.fromEntityToReceivedAsks(answerUser, asks);
  }
}
