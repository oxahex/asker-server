package oxahex.asker.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.dto.user.dto.UserDto.UserAnswersDto;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

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
}
