package oxahex.asker.dispatch;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dispatch.dto.AskDto.AskListDto;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.common.ResponseDto;
import oxahex.asker.utils.ValidUtil;

@Slf4j
@RestController
@RequestMapping("/api/asks")
@RequiredArgsConstructor
public class AskController {

  private final AskService askService;
  private final DispatchService dispatchService;

  @PostMapping
  @PreAuthorize("permitAll()")
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
  @GetMapping
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<ResponseDto<AskListDto>> getReceivedAsks(
      @AuthenticationPrincipal AuthUser authUser,
      @RequestParam Long userId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "desc") SortType sortType
  ) {

    log.info("[받은 질문 목록 조회] userId={}, sort={}", userId, sortType.getCondition());

    // 본인이 받은 질문에 대한 요청인지 검증
    ValidUtil.validateUser(authUser, userId);
    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(sortType.getDirection(), "createdDateTime"));

    AskListDto receivedAsks =
        askService.getAsks(authUser.getUser(), pageRequest);

    return ResponseEntity.ok(new ResponseDto<>(200, "", receivedAsks));
  }
}
