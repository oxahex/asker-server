package oxahex.asker.domain.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserError {

  NOT_FOUND_EMAIL(404, "등록되지 않은 이메일입니다."),
  NOT_FOUND_USER(404, "해당 유저를 찾을 수 없습니다."),
  ALREADY_EXIST_EMAIL(409, "이미 존재하는 이메일입니다.");

  private final int statusCode;
  private final String errorMessage;
}
