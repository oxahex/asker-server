package oxahex.asker.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.dto.auth.JoinDto.JoinReqDto;
import oxahex.asker.dto.auth.JoinDto.JoinResDto;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/join")
  public ResponseEntity<ResponseDto<JoinResDto>> join(
      @RequestBody @Valid JoinReqDto joinReqDto,
      BindingResult bindingResult
  ) {

    log.info("[AuthController.join] name={}, email={}, password={}",
        joinReqDto.getName(),
        joinReqDto.getEmail(), joinReqDto.getPassword());

    JoinResDto joinUser = authService.join(joinReqDto);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "회원가입이 완료되었습니다.", joinUser), HttpStatus.CREATED
    );
  }
}
