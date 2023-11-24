package oxahex.asker.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class UserReqDto {

  @Getter
  @Setter
  public static class JoinReqDto {

    @NotEmpty(message = "이름(닉네임)을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣]{2,12}", message = "한글 또는 영문으로 이루어진 최소 2글자, 최대 12자의 이름을 입력해주세요.")
    private String name;

    // 최대 30자
    @NotEmpty(message = "로그인에 사용할 이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-_.]{1,30}@[a-zA-Z0-9-]+\\.[a-zA-Z]+$", message = "올바른 이메일 형식이 아닙니다(이메일 아이디는 최대 30자까지 입력 가능합니다).")
    @Size(max = 50, message = "50자 미만의 이메일을 입력해주세요.")
    private String email;

    // 8 ~ 20
    @NotEmpty(message = "로그인에 사용할 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "최소 8글자, 최대 20글자의 비밀번호를 입력해주세요.")
    private String password;
  }

  @Getter
  @Setter
  public static class LoginReqDto {

    @NotEmpty(message = "로그인에 사용할 이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-_.]{1,30}@[a-zA-Z0-9-]+\\.[a-zA-Z]+$", message = "올바른 이메일 형식이 아닙니다(이메일 아이디는 최대 30자까지 입력 가능합니다).")
    private String email;

    @NotEmpty(message = "로그인에 사용할 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "최소 8글자, 최대 20글자의 비밀번호를 입력해주세요.")
    private String password;
  }
}
