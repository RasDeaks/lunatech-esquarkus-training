package fr.lunatech.elasticsearch.config;

import fr.lunatech.elasticsearch.client.ElasticClient;
import fr.lunatech.elasticsearch.client.ElasticClientWrap;
import org.elasticsearch.client.RestHighLevelClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class ElasticsearchClientConfig {

    @Produces
    @ApplicationScoped
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
