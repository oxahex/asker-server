package oxahex.asker.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxahex.asker.auth.dto.TokenDto;
import oxahex.asker.common.ResponseDto;
import oxahex.asker.auth.dto.JoinDto.JoinReqDto;
import oxahex.asker.auth.dto.JoinDto.JoinResDto;

@Slf4j
@RestController
@PreAuthorize("permitAll()")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String JWT_HEADER_NAME = "Authorization";
  private static final String JWT_HEADER_PREFIX = "Bearer ";

  private final AuthService authService;

  @PostMapping("/join")
  public ResponseEntity<ResponseDto<JoinResDto>> join(
      @RequestBody @Valid JoinReqDto joinReqDto
  ) {

    log.info("[AuthController.join] name={}, email={}, password={}",
        joinReqDto.getName(),
        joinReqDto.getEmail(), joinReqDto.getPassword());

    JoinResDto joinUser = authService.join(joinReqDto);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "회원가입이 완료되었습니다.", joinUser), HttpStatus.CREATED
    );
  }

  /**
   * JWT Access Token 재발급
   * <p> Access Token 만료 시 프론트에서 Authorization Header에 Refresh Token 전송
   *
   * @return JWT Access Token
   */
  @PostMapping(path = "/token", headers = JWT_HEADER_NAME)
  public ResponseEntity<ResponseDto<TokenDto>> token(
      @RequestHeader(JWT_HEADER_NAME) String refreshToken,
      @AuthenticationPrincipal AuthUser authUser
  ) {

    log.info("[Refresh Access Token] refreshToken={}", refreshToken);
    TokenDto tokenDto = authService.refreshAccessToken(authUser, refreshToken);

    return new ResponseEntity<>(
        new ResponseDto<>(201, "토큰이 정상적으로 재발급 되었습니다.", tokenDto), HttpStatus.CREATED
    );
  }
}
