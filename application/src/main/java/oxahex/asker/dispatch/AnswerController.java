package oxahex.asker.dispatch;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.AuthUser;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.dto.ResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

  private final DispatchService dispatchService;

  @PostMapping
  public ResponseEntity<ResponseDto<AnswerInfoDto>> answer(
      @RequestBody @Valid AnswerDto.AnswerReqDto answerReqDto,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[AnswerController.answer] ask_id={}, contents={}",
        answerReqDto.getAskId(), answerReqDto.getContents());

    Answer answer = dispatchService.dispatchAnswer(authUser.getUser(), answerReqDto);
    AnswerInfoDto answerInfo = AnswerDto.fromEntityToAnswerInfo(answer);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "성공적으로 답변했습니다.", answerInfo), HttpStatus.CREATED
    );
  }

}
