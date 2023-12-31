package oxahex.asker.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import oxahex.asker.domain.error.exception.DispatchException;
import oxahex.asker.domain.error.exception.UserException;
import oxahex.asker.common.ResponseDto;
import oxahex.asker.error.exception.ServiceException;
import oxahex.asker.error.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
        .code(e.getStatusCode())
        .message(e.getErrorMessage())
        .data(null).build();

    return new ResponseEntity<>(response,
        HttpStatus.valueOf(e.getStatusCode()));
  }

  /**
   * 서비스 관련 에러 핸들링
   *
   * @param e UserException
   * @return Error Response with custom UserException Status Code
   */
  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<?> serviceException(ServiceException e) {
    log.error(e.getErrorMessage());

    ResponseDto<?> response = ResponseDto.builder()
        .code(e.getHttpStatus().value())
        .message(e.getErrorMessage())
        .data(null).build();

    return new ResponseEntity<>(response, e.getHttpStatus());
  }

  /**
   * 질문/답변 관련 에러 핸들링
   *
   * @param e UserException
   * @return Error Response with custom UserException Status Code
   */
  @ExceptionHandler(DispatchException.class)
  public ResponseEntity<?> dispatchException(DispatchException e) {
    log.error(e.getErrorMessage());

    ResponseDto<?> response = ResponseDto.builder()
        .code(e.getStatusCode())
        .message(e.getErrorMessage())
        .data(null).build();

    return new ResponseEntity<>(response,
        HttpStatus.valueOf(e.getStatusCode()));
  }

  /**
   * 유효성 검사 관련 에러 핸들링
   *
   * @param e ValidationException
   * @return Error Response with BAD_REQUEST(400)
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<?> validationException(ValidationException e) {

    log.error(e.getMessage());

    ResponseDto<?> response = ResponseDto.builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .data(e.getErrorMap()).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
