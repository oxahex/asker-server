package oxahex.asker.aop;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import oxahex.asker.error.exception.ValidationException;

@Component
@Aspect
public class RequestValidationAop {

  @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
  public void postMapping() {
  }

  @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
  public void putMapping() {
  }

  /**
   * Request Body 데이터 유효성 체크
   * <p>POST, PUT Request 전후 핸들링
   */
  @Around("postMapping() || putMapping()")
  public Object requestValidationAdvice(ProceedingJoinPoint pjp) throws Throwable {

    Object[] args = pjp.getArgs();
    for (Object arg : args) {

      if (arg instanceof BindingResult) {
        BindingResult bindingResult = (BindingResult) arg;

        if (bindingResult.hasErrors()) {
          Map<String, String> errorMap = new HashMap<>();

          for (FieldError error : bindingResult.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
          }

          // Validation Exception
          throw new ValidationException("유효성 검사 실패", errorMap);
        }
      }
    }

    return pjp.proceed();
  }
}
