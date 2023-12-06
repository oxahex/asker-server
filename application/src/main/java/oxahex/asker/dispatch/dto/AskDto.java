package oxahex.asker.dispatch.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.dto.user.dto.UserDto;
import oxahex.asker.dto.user.dto.UserDto.UserInfoDto;

public class AskDto {

  @Getter
  @Setter
  public static class AskReqDto {

    @NotNull
    @Max(value = Long.MAX_VALUE, message = "올바른 값이 아닙니다.")
    private Long answerUserId;

    @NotEmpty(message = "질문 내용을 입력해주세요.")
    @Pattern(regexp = "^.{10,800}$", message = "질문 내용은 최소 10자, 최대 800자 까지 입력할 수 있습니다.")
    private String contents;
  }

  @Getter
  @Setter
  public static class AskInfoDto {

    private Long askId;
    private UserInfoDto askUserInfo;
    private UserInfoDto answerUserInfo;
    private String contents;
  }

  public static AskInfoDto fromEntityToAskInfo(Dispatch dispatch) {

    AskInfoDto askInfoDto = new AskInfoDto();
    askInfoDto.setAskId(dispatch.getAsk().getId());

    if (dispatch.getAsk().getAskUser() != null) {
      askInfoDto.setAskUserInfo(UserDto.fromEntityToUserInfo(dispatch.getAsk().getAskUser()));
    }

    askInfoDto.setAnswerUserInfo(UserDto.fromEntityToUserInfo(dispatch.getAnswerUser()));
    askInfoDto.setContents(dispatch.getAsk().getContents());

    return askInfoDto;
  }

}
