package oxahex.asker.user;

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
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.dto.user.dto.UserDto.UserAnswersDto;
import oxahex.asker.dto.user.dto.UserDto.UserAsksDto;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.type.ServiceError;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}/asks")
  public ResponseEntity<ResponseDto<UserAsksDto>> getReceivedAsks(
      @PathVariable Long userId,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[UserController.getDispatches] 유저 아이디={}", userId);

    // 로그인 유저 본인의 질문 목록 조회만 허용
    validateUser(authUser, userId);

    UserAsksDto userAsks = userService.getReceivedAsks(userId);

    return ResponseEntity.ok(new ResponseDto<>(200, "", userAsks));
  }

  /**
   * 특정 유저의 답변 목록 조회)
   *
   * @param userId 특정 유저의 ID
   * @return 답변 목록
   */
  @GetMapping("/{userId}/answers")
  public ResponseEntity<ResponseDto<?>> getUserAnswers(
      @PathVariable Long userId
  ) {

    log.info("[특정 유저의 답변 목록 조회] 특정 유저 아이디={}", userId);

    UserAnswersDto userAnswers = userService.getUserAnswers(userId);

    return ResponseEntity.ok(new ResponseDto<>(200, "", userAnswers));
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
