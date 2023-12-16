package oxahex.asker.search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import oxahex.asker.dispatch.dto.AnswerDto;
import oxahex.asker.dispatch.dto.AnswerDto.AnswerInfoDto;
import oxahex.asker.dispatch.dto.AskDto;
import oxahex.asker.dispatch.dto.AskDto.AskInfoDto;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "answer_idx")
@Setting(settingPath = "/elasticsearch/setting.json")
@Mapping(mappingPath = "/elasticsearch/mapping.json")
public class AnswerDocument {

  @Id
  private Long id;  // 답변 ID

  private String ask;
  private String answer;

  public static AnswerDocument fromEntityToAnswerDocument(
      Ask ask,
      Answer answer
  ) {

    return AnswerDocument.builder()
        .id(answer.getId())
        .ask(ask.getContents())
        .answer(answer.getContents())
        .build();
  }
}
