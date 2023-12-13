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
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.dto.ResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/asks")
@RequiredArgsConstructor
public class AskController {

  private final DispatchService dispatchService;

  @PostMapping
  public ResponseEntity<ResponseDto<AskInfoDto>> ask(
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
}
