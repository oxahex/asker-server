package oxahex.asker.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserError {

  NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."),
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
  ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;
}
