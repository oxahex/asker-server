package oxahex.asker.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceError {

  NO_AUTHORITY_TO_ACCESS(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
  NO_AUTHORITY_TO_ANSWER(HttpStatus.UNAUTHORIZED, "답변 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;
}
