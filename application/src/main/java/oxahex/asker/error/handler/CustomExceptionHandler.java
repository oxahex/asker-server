package oxahex.asker.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import oxahex.asker.dto.ResponseDto;
import oxahex.asker.error.exception.UserException;

@Slf4j
@Component
public class CustomExceptionHandler {

  /**
   * 유저 관련 에러 핸들링
   *
   * @param e UserException
   * @return Error Response with custom UserException Status Code
   */
  @ExceptionHandler(UserException.class)
  public ResponseEntity<?> userException(UserException e) {
    log.error(e.getErrorMessage());

    ResponseDto<?> response = ResponseDto.builder()
        .code(e.getHttpStatus().value())
        .message(e.getErrorMessage())
        .data(null).build();

    return new ResponseEntity<>(response, e.getHttpStatus());
  }
}
