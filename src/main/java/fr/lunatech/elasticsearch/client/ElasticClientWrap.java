package fr.lunatech.elasticsearch.client;

import io.vavr.CheckedFunction2;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static io.vavr.control.Try.of;
import static io.vavr.control.Try.success;
import static lombok.AccessLevel.*;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.search.fetch.subphase.FetchSourceContext.DO_NOT_FETCH_SOURCE;
import static org.elasticsearch.search.fetch.subphase.FetchSourceContext.FETCH_SOURCE;

@Slf4j
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ElasticClientWrap implements ElasticClient {

    public static final Consumer<? super Throwable> LOG_ERROR = e -> log.error("ElasticsearchClient", e);

    @NonNull
    CheckedFunction2<IndexRequest, RequestOptions, IndexResponse> index;
    @NonNull
    CheckedFunction2<GetRequest, RequestOptions, Boolean> exists;
    @NonNull
    CheckedFunction2<BulkRequest, RequestOptions, BulkResponse> bulk;
    @NonNull
    CheckedFunction2<GetRequest, RequestOptions, GetResponse> get;
    @NonNull
    CheckedFunction2<MultiGetRequest, RequestOptions, MultiGetResponse> multiGet;
    @NonNull
    CheckedFunction2<SearchRequest, RequestOptions, SearchResponse> search;
    @NonNull
    CheckedFunction2<SearchScrollRequest, RequestOptions, SearchResponse> searchScroll;
    @NonNull
    CheckedFunction2<ClearScrollRequest, RequestOptions, ClearScrollResponse> clearScroll;

    @Override
    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) {
        return success(indexRequest).mapTry(r -> index.apply(r,options)).get();
    }

    @Override
    public boolean exists(final String index, final String id) {
        return success(new GetRequest(index, id))
                .map(r -> r.fetchSourceContext(DO_NOT_FETCH_SOURCE))
                .mapTry(r -> exists.apply(r, DEFAULT))
                .onFailure(LOG_ERROR)
                .get();
    }

    @Override
    public BulkItemResponse[] bulk(final BulkRequest request) {
        return of(() -> bulk.apply(request, DEFAULT))
                .map(BulkResponse::getItems)
                .onFailure(LOG_ERROR)
                .getOrElse(new BulkItemResponse[0]);
    }

    @Override
    public Optional<String> get(final String index, final String id) {
        return success(new GetRequest(index, id))
                .map(r -> r.fetchSourceContext(FETCH_SOURCE))
                .mapTry(r -> get.apply(r, DEFAULT))
                .filter(GetResponse::isExists)
                .map(GetResponse::getSourceAsString)
                .onFailure(LOG_ERROR)
                .toJavaOptional();
    }

    @Override
    public MultiGetItemResponse[] multiGet(final String index,
                                           final List<String> ids) {
        final MultiGetRequest multi = new MultiGetRequest();
        ids.forEach(id -> multi.add(index, id));

        return success(multi)
                .mapTry(r -> multiGet.apply(r, DEFAULT))
                .map(MultiGetResponse::getResponses)
                .getOrElse(new MultiGetItemResponse[0]);
    }

    @Override
    public SearchResponse search(final SearchRequest request) {
        return success(request).mapTry(r -> search.apply(r, DEFAULT)).get();
    }

    @Override
    public SearchResponse searchScroll(final SearchScrollRequest request) {
        return success(request).mapTry(r -> searchScroll.apply(r, DEFAULT)).get();
    }

    @Override
    public void clearScroll(final String scrollId) {
        final ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(scrollId);

        of(() -> clearScroll.apply(request, DEFAULT));
    }

}
