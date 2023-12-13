package oxahex.asker.domain.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchError {

  ASK_NOT_FOUND(404, "존재하지 않는 질문입니다."),
  ANSWER_NOT_FOUND(404, "존재하지 않는 답변입니다."),
  DISPATCH_NOT_FOUND(404, "존재하지 않는 내역입니다."),

  ALREADY_EXIST_ANSWER(409, "이미 답변이 있습니다.");

  private final int statusCode;
  private final String errorMessage;
}
