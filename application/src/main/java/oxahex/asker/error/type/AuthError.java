package oxahex.asker.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthError {

  AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다. 아이디와 비밀번호를 확인해주세요."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;
}
