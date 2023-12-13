package oxahex.asker.dispatch;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.ReceivedAsksDto;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.utils.ValidUtil;

@Slf4j
@RestController
@RequestMapping("/api/asks")
@RequiredArgsConstructor
public class AskController {

  private final AskService askService;
  private final DispatchService dispatchService;

  @PostMapping
  public ResponseEntity<ResponseDto<AskInfoDto>> dispatchAsk(
      @RequestBody @Valid AskDto.AskReqDto askReqDto,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[AskController.ask] ask_user_id={}, contents={}",
        askReqDto.getAnswerUserId(), askReqDto.getContents());

    AskInfoDto askInfo = dispatchService.dispatchAsk(authUser, askReqDto);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "성공적으로 질문했습니다.", askInfo), HttpStatus.CREATED
    );
  }

  /**
   * 특정 유저가 받은 질문 조회
   *
   * @return 질문 목록
   */
  @PreAuthorize("hasRole('USER')")
  @GetMapping("?userId={userId}&sortType={sort}")
  public ResponseEntity<?> getAsksByUser(
      @AuthenticationPrincipal AuthUser authUser,
      @PathParam("userId") Long userId,
      @PathParam("sort") String sort
  ) {

    // 본인이 받은 질문에 대한 요청인지 검증
    ValidUtil.validateUser(authUser, userId);

    // 정렬 조건
    SortType sortType = SortType.getSortType(sort);

    ReceivedAsksDto receivedAsks =
        askService.getReceivedAsks(authUser.getUser(), sortType);

    return ResponseEntity.ok(new ResponseDto<>(200, "", receivedAsks));
  }
}
