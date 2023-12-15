package oxahex.asker.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.common.ResponseDto;
import oxahex.asker.notification.dto.NotificationDto.NotificationListDto;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * 읽지 않은 모든 알림 목록 조회
   *
   * @param authUser 로그인 유저
   * @return 읽지 않은 알림 내역
   */
  @GetMapping
  public ResponseEntity<ResponseDto<NotificationListDto>> getNotifications(
      @AuthenticationPrincipal AuthUser authUser,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size
  ) {

    log.info("[유저 알림 조회] userEmail={}", authUser.getUsername());

    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(Direction.DESC, "createdDateTime"));

    NotificationListDto notifications =
        notificationService.getNotifications(authUser, pageRequest);

    return ResponseEntity.ok(new ResponseDto<>(200, "", notifications));
  }
}
