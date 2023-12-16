package oxahex.asker.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.Query.Kind;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import oxahex.asker.domain.answer.Answer;
import oxahex.asker.domain.ask.Ask;
import oxahex.asker.error.type.ServiceError;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final SearchRepository searchRepository;
  private final ElasticsearchClient esClient;


  public void saveAnswer(Ask ask, Answer answer) {

    log.info("[ES 답변 저장]");
    searchRepository.save(AnswerDocument
        .fromEntityToAnswerDocument(ask, answer));
  }

  public List<AnswerDocument> searchAnswers(String keyword, PageRequest pageRequest) {

    log.info("[답변 겸색] keyword={}", keyword);

    try {

      SearchResponse<AnswerDocument> search = esClient.search(searchRequest -> searchRequest
          .index("answer_idx")
          .query(queryBuilder -> queryBuilder.match(matchQBuilder ->
              matchQBuilder.field("answer")
                  .query(keyword))), AnswerDocument.class);
      

    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
