package fr.lunatech.elasticsearch.config;

import fr.lunatech.elasticsearch.client.ElasticClient;
import fr.lunatech.elasticsearch.client.ElasticClientWrap;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchClientConfig {

    @Bean
    ElasticClient elasticClientWrap(final RestHighLevelClient client) {
        return new ElasticClientWrap(
                client::index,
                client::exists,
                client::bulk,
                client::get,
                client::mget,
                client::search,
                client::scroll,
                client::clearScroll
        );
    }

}
