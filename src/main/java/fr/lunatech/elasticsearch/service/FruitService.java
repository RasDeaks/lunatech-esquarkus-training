package fr.lunatech.elasticsearch.service;

import fr.lunatech.elasticsearch.model.Fruit;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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

/**
 * Fruit Service
 * Create, get and search (by attribute) a {@link Fruit}
 */
@Slf4j
@ApplicationScoped
public class FruitService {

    private static final String FRUITT_INDEX_NAME= "fruits";

    @Inject
    RestHighLevelClient restHighLevelClient;

    public void create(Fruit fruit) throws IOException {
        log.debug("Indexing FRUIT [{}] in ES", fruit.getName());
        IndexRequest request = new IndexRequest(FRUITT_INDEX_NAME);
        request.id(fruit.getId());
        request.source(JsonObject.mapFrom(fruit).toString(), XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
        log.debug("FRUIT [{}] indexed ", fruit.getName());
    }

    public Fruit get(String id) throws IOException {
        log.debug("Get FRUIT by ID : [{}]", id);
        GetRequest getRequest = new GetRequest("fruits", id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            log.debug("OK => Fruit:{}", sourceAsString);
            JsonObject json = new JsonObject(sourceAsString);
            return json.mapTo(Fruit.class);
        }
        log.debug("FRUIT [{}] not found!", id);
        return null;
    }

    public List<Fruit> searchByColor(String color) throws IOException {
        return search("color", color);
    }

    public List<Fruit> searchByName(String name) throws IOException {
        return search("name", name);
    }

    private List<Fruit> search(String term, String match) throws IOException {
        log.debug("Search FRUIT by {}: [{}]", term, match);
        // prepare request
        SearchRequest searchRequest = new SearchRequest(FRUITT_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(term, match));
        searchRequest.source(searchSourceBuilder);

        // call ES
        log.debug("ES query = {}", Json.encode(searchRequest));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        log.debug("ES response = {}", Json.encode(searchResponse));
        SearchHits hits = searchResponse.getHits();
        List<Fruit> results = new ArrayList<>(hits.getHits().length);
        log.debug("  {} hit found !", hits.getHits().length);

        // map JSON response to model & return
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            JsonObject json = new JsonObject(sourceAsString);
            results.add(json.mapTo(Fruit.class));
        }
        return results;
    }


}
