package oxahex.asker.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final ElasticsearchClient esClient;


  public void createIndex(String indexName) {
    try {
      esClient.indices().create(c -> c.index(indexName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void saveAnswer(Ask ask, Answer answer) {

    log.info("[ES 답변 저장] askId={}, answerId={}", ask.getId(), answer.getId());

    AnswerDocument answerDocument =
        AnswerDocument.fromEntityToAnswerDocument(ask, answer);

    try {
      esClient.index(i -> i
          .index("answer_idx")
          .id(ask.getId().toString())
          .document(answerDocument));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<AnswerDocument> searchAnswers(String keyword, PageRequest pageRequest) {

    log.info("[답변 겸색] keyword={}", keyword);
    List<AnswerDocument> answerDocuments = new ArrayList<>();

    Query byAskKeyword = MatchQuery.of(m ->
        m.field("ask").query(keyword))._toQuery();

    Query byAnswerKeyword = MatchQuery.of(m ->
        m.field("answer").query(keyword))._toQuery();

    try {

      SearchResponse<AnswerDocument> response = esClient.search(s -> s
              .index("answer_idx")
              .query(q -> q
                  .bool(b -> b
                      .should(byAskKeyword)
                      .should(byAnswerKeyword)
                  )
              ),
          AnswerDocument.class
      );

      TotalHits totalHits = response.hits().total();

      List<Hit<AnswerDocument>> hits = response.hits().hits();
      for (Hit<AnswerDocument> hit : hits) {
        answerDocuments.add(hit.source());
      }
    } catch (IOException e) {
      throw new RuntimeException();
    }

    return answerDocuments;
  }
}
