package oxahex.asker.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.dispatch.DispatchDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final DispatchDomainService dispatchDomainService;

  public List<AskInfoDto> getReceivedAsks(Long userId) {

    // 받은 질문 내역 확인
    List<Dispatch> dispatches = dispatchDomainService.findDispatches(userId);

    // 질문 전송 내역에서 질문 추출, 없으면 null 반환
    return dispatches.stream()
        .map(dispatch -> AskDto.fromEntityToAskInfo(dispatch.getAsk()))
        .toList();
  }
}
