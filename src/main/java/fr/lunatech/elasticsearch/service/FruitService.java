package fr.lunatech.elasticsearch.service;

import fr.lunatech.elasticsearch.client.ElasticClient;
import fr.lunatech.elasticsearch.model.Fruit;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fruit Service
 * Create, get and search (by attribute) a {@link Fruit}
 */
@Slf4j
@ApplicationScoped
public class FruitService {

    private static final String FRUIT_INDEX_NAME = "fruits";

    ElasticClient elasticClient;

    @Inject
    public FruitService(ElasticClient elasticClientWrap) {
        this.elasticClient = elasticClientWrap;
    }

    public void create(Fruit fruit) throws IOException {
        log.debug("Indexing FRUIT [{}] in ES", fruit.getName());
        IndexRequest request = new IndexRequest(FRUIT_INDEX_NAME);
        request.id(fruit.getId());
        request.source(JsonObject.mapFrom(fruit).toString(), XContentType.JSON);
        elasticClient.index(request, RequestOptions.DEFAULT);
        log.debug("FRUIT [{}] indexed ", fruit.getName());
    }

    public Fruit get(String id) {
        log.debug("Get FRUIT by ID : [{}]", id);
        Optional<String> getResponse = elasticClient.get(FRUIT_INDEX_NAME, id);
        if (getResponse.isPresent()) {
            String sourceAsString = getResponse.get();
            log.debug("OK => Fruit:{}", sourceAsString);
            JsonObject json = new JsonObject(sourceAsString);
            return json.mapTo(Fruit.class);
        }
        log.debug("FRUIT [{}] not found!", id);
        return null;
    }

    public List<Fruit> searchByColor(String color) {
        return search("color", color);
    }

    public List<Fruit> searchByName(String name) {
        return search("name", name);
    }

    private List<Fruit> search(String term, String match) {
        log.debug("Search FRUIT by {}: [{}]", term, match);
        // prepare request
        SearchRequest searchRequest = new SearchRequest(FRUIT_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(term, match));
        searchRequest.source(searchSourceBuilder);

        // call ES
        log.trace("ES query = {}", Json.encode(searchRequest));
        SearchResponse searchResponse = elasticClient.search(searchRequest);
        log.debug("ES response = {}", Json.encode(searchResponse));
        SearchHits hits = searchResponse.getHits();
        List<Fruit> results = new ArrayList<>(hits.getHits().length);
        log.trace("  {} hit found !", hits.getHits().length);

        // map JSON response to model & return
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            JsonObject json = new JsonObject(sourceAsString);
            results.add(json.mapTo(Fruit.class));
        }
        return results;
    }


}
