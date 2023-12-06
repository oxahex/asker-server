package oxahex.asker.domain.ask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oxahex.asker.domain.user.User;
import oxahex.asker.domain.user.UserDomainService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskDomainService {

  private final AskRepository askRepository;
  private final UserDomainService userDomainService;


  /**
   * 로그인 유저 질문 생성
   *
   * @param askUser  질문 유저(Nullable)
   * @param contents 질문 내용
   * @return 생성된 질문
   */
  @Transactional
  public Ask createAsk(User askUser, String contents) {

    return askRepository.save(Ask.builder()
        .askUser(askUser)
        .contents(contents)
        .askType(AskType.USER)
        .build()
    );
  }

  // 질문 삭제
  public Ask deleteAsk() {

    return null;
  }

}
