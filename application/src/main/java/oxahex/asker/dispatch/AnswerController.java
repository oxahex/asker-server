package oxahex.asker.dispatch;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerListDto;
import oxahex.asker.domain.condition.SortType;
import oxahex.asker.common.ResponseDto;
import oxahex.asker.search.AnswerDocument;

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
  public ResponseEntity<ResponseDto<?>> getAnswers(
      @RequestParam Long userId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "desc") SortType sortType
  ) {

    log.info("[특정 유저의 답변 목록 조회] 특정 유저 아이디={}", userId);

    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(sortType.getDirection(), "createdDateTime"));

    AnswerListDto postedAnswers = answerService.getAnswers(userId, pageRequest);

    return ResponseEntity.ok(new ResponseDto<>(200, "", postedAnswers));
  }

  @GetMapping("/search")
  @PreAuthorize("permitAll()")
  public ResponseEntity<?> searchAnswers(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size
  ) {

    log.info("[답변 검색] keyword={}", keyword);
    PageRequest pageRequest = PageRequest.of(page, size);

    Page<AnswerDocument> answerDocuments =
        answerService.searchAnswers(keyword, pageRequest);

    log.info("[답변 검색 결과] result={}", answerDocuments.get());

    return ResponseEntity.ok(new ResponseDto<>(200, "", answerDocuments));
  }
}
