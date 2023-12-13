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
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AnswerDto.PostedAnswersDto;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.dto.ResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

  private final DispatchService dispatchService;
  private final AnswerService answerService;

  @PostMapping
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<ResponseDto<AnswerInfoDto>> dispatchAnswer(
      @RequestBody @Valid AnswerDto.AnswerReqDto answerReqDto,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[AnswerController.answer] ask_id={}, contents={}",
        answerReqDto.getAskId(), answerReqDto.getContents());

    AnswerInfoDto answerInfo = dispatchService.dispatchAnswer(authUser.getUser(), answerReqDto);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "성공적으로 답변했습니다.", answerInfo), HttpStatus.CREATED
    );
  }

  /**
   * 특정 유저의 답변 목록 조회)
   *
   * @param userId 특정 유저의 ID
   * @return 답변 목록
   */
  @GetMapping
  @PreAuthorize("permitAll()")
  public ResponseEntity<ResponseDto<?>> getUserAnswers(
      @PathParam("userId") Long userId,
      @PathParam("sort") String sort
  ) {

    log.info("[특정 유저의 답변 목록 조회] 특정 유저 아이디={}", userId);

    SortType sortType = SortType.getSortType(sort);
    PostedAnswersDto postedAnswers = answerService.getPostedAnswers(userId, sortType);

    return ResponseEntity.ok(new ResponseDto<>(200, "", postedAnswers));
  }
}
