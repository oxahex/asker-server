package oxahex.asker.user;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.type.ServiceError;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}/asks")
  public ResponseEntity<ResponseDto<?>> getReceivedAsks(
      @PathVariable Long userId,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[UserController.getDispatches] 유저 아이디={}", userId);

    // 로그인 유저 본인의 질문 목록 조회만 허용
    validateUser(authUser, userId);

    List<AskInfoDto> receivedAskInfos = userService.getReceivedAsks(userId);

    return ResponseEntity.ok(new ResponseDto<>(200, "", receivedAskInfos));
  }

  /**
   * 본인에 대한 정보 요청이 아닌 경우 예외 처리
   *
   * @param authUser 로그인 유저 정보
   * @param userId   요청 유저 ID
   */
  private void validateUser(AuthUser authUser, Long userId) {
    if (!Objects.equals(authUser.getUser().getId(), userId)) {
      throw new ServiceException(ServiceError.NO_AUTHORITY_TO_ACCESS);
    }
  }
}
