package oxahex.asker.auth.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.dispatch.Dispatch;
import oxahex.asker.domain.user.User;

public class UserDto {

  @Getter
  @Setter
  public static class UserInfoDto {

    private Long id;
    private String name;
    private String email;
  }

  public static UserInfoDto fromEntityToUserInfo(User user) {
    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setId(user.getId());
    userInfo.setName(user.getName());
    userInfo.setEmail(user.getEmail());

    return userInfo;
  }

  @Getter
  @Setter
  public static class UserAsksDto {

    private Long id;
    private String name;
    private List<AskInfoDto> asks;
  }

  public static UserAsksDto fromEntityToUserAsks(
      User answerUser,
      List<Dispatch> dispatches
  ) {
    UserAsksDto userAsks = new UserAsksDto();
    userAsks.setId(answerUser.getId());
    userAsks.setName(answerUser.getName());
    userAsks.setAsks(dispatches.stream()
        .map(dispatch -> AskDto.fromEntityToAskInfo(dispatch.getAsk()))
        .toList()
    );

    return userAsks;
  }

  @Getter
  @Setter
  public static class UserAnswersDto {

    private Long id;
    private String name;
    private List<AnswerInfoDto> answers;
  }

  public static UserAnswersDto fromEntityToUserAnswers(
      User answerUser,
      List<Answer> answers
  ) {

    UserAnswersDto userAnswers = new UserAnswersDto();
    userAnswers.setId(answerUser.getId());
    userAnswers.setName(answerUser.getName());
    userAnswers.setAnswers(answers.stream()
        .map(AnswerDto::fromEntityToAnswerInfo)
        .toList());

    return userAnswers;
  }
}
