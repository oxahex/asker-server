package oxahex.asker.utils;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.type.ServiceError;

@Slf4j
@UtilityClass
public class ValidUtil {

  /**
   * 본인에 대한 정보 요청이 아닌 경우 예외 처리
   *
   * @param authUser 로그인 유저 정보
   * @param userId   요청 유저 ID
   */
  public void validateUser(AuthUser authUser, Long userId) {
    if (!Objects.equals(authUser.getUser().getId(), userId)) {
      log.error("[유저 본인 확인] 로그인 유저 본인이 아님");
      throw new ServiceException(ServiceError.NO_AUTHORITY_TO_ACCESS);
    }
  }

}
