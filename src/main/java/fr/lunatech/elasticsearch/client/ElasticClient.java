package fr.lunatech.elasticsearch.client;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ElasticClient {

    IndexResponse index(IndexRequest indexRequest, RequestOptions options) throws IOException;

    boolean exists(String index, String id);

    BulkItemResponse[] bulk(BulkRequest request);

    Optional<String> get(String index, String id);

    MultiGetItemResponse[] multiGet(String index, List<String> ids);

    SearchResponse search(SearchRequest request);

    SearchResponse searchScroll(SearchScrollRequest request);

    void clearScroll(String scrollId);
}
