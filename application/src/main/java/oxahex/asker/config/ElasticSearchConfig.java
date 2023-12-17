package oxahex.asker.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfig {

  private static final String ES_URL = "http://localhost:9200";

  @Bean
  public ElasticsearchClient elasticsearchClient() {
    RestClient restClient = RestClient
        .builder(HttpHost.create(ES_URL))
        .build();
    ElasticsearchTransport transport = new RestClientTransport(
        restClient, new JacksonJsonpMapper());
    return new ElasticsearchClient(transport);
  }
}
