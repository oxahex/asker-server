package oxahex.asker.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends ElasticsearchRepository<AnswerDocument, Long> {

  @Query("{\"match\": {\"answer\": {\"query\": \"?0\"}}}")
  Page<AnswerDocument> findAllByKeyword(String keyword, PageRequest pageRequest);
}
