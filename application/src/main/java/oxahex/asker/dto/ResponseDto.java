package oxahex.asker.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

  private final Integer code;
  private final String message;
  private final T data;

  @Builder
  public ResponseDto(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }
}
