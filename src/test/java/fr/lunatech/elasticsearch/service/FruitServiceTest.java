package fr.lunatech.elasticsearch.service;

import fr.lunatech.elasticsearch.model.Fruit;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


@QuarkusTest
class FruitServiceTest {

    @Inject
    FruitService fruitService;

    //test using a mock service class (FruitServiceMock.class), never fail
    @Test
    void testSearchByName_ClassMock() throws IOException {
        List<Fruit> pasteque = fruitService.searchByName("pasteque");
        pasteque.forEach(System.out::println);
        Assertions.assertEquals(1,pasteque.size());
    }

    // test using a local mock
    @Test
    void testGetAll_LocalMock() throws IOException{
        FruitService service = Mockito.mock(FruitService.class);
        Fruit mockResponse = new Fruit();
        mockResponse.setName("Peche de test");
        mockResponse.setDescription("Pas vraiment une peche");
        Mockito.when(service.getAll()).thenReturn(Collections.singletonList(mockResponse));

        List<Fruit> all = service.getAll();
        all.forEach(System.out::println);
        Assertions.assertEquals(1, all.size());
    }

    // hybrid test : restEasy + QuarkusMock
    @Test
    void testPostFruit() throws IOException{
        String uuid = UUID.randomUUID().toString();
        Fruit testFruit = new Fruit();
        testFruit.setName("pomme");
        testFruit.setDescription("pomme de test");
        testFruit.setId(uuid);

        FruitServiceMock mockService = Mockito.mock(FruitServiceMock.class);
        Mockito.doNothing().when(mockService).create(testFruit);

        QuarkusMock.installMockForType(mockService, FruitServiceMock.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Peche\",\"description\":\"Blanche\"}")
                .post("/fruits")
                .then()
                .statusCode(201);
    }

    // test using restEasy : real call, fail if ES involved in call (search or Post result in connection refused)
    @Test
    void testGetHomePage() {
        given()
                .when().get("/fruit.html")
                .then()
                .statusCode(200)
                .body(containsString("REST Service - Fruit"));
    }

    // in case you need to mock an ES response
    public String jsonResponse(){
        return "{\"internalResponse\":{\"numReducePhases\":1,\"fragment\":true},\"scrollId\":null,\"totalShards\":1,\"successfulShards\":1,\"skippedShards\":0,\"shardFailures\":[],\"clusters\":{\"total\":0,\"successful\":0,\"skipped\":0,\"fragment\":true},\"took\":{\"seconds\":0,\"days\":0,\"hours\":0,\"minutes\":0,\"millis\":3,\"nanos\":3000000,\"stringRep\":\"3ms\",\"micros\":3000,\"microsFrac\":3000.0,\"millisFrac\":3.0,\"secondsFrac\":0.003,\"minutesFrac\":5.0E-5,\"hoursFrac\":8.333333333333333E-7,\"daysFrac\":3.472222222222222E-8},\"numReducePhases\":1,\"timedOut\":false,\"terminatedEarly\":null,\"failedShards\":0,\"aggregations\":null,\"suggest\":null,\"profileResults\":{},\"hits\":{\"hits\":[{\"score\":1.6455007,\"id\":\"d3782cf5-5be6-4358-91b1-11ce5ef43352\",\"type\":\"_doc\",\"nestedIdentity\":null,\"version\":-1,\"seqNo\":-2,\"primaryTerm\":0,\"highlightFields\":{},\"sortValues\":[],\"matchedQueries\":[],\"explanation\":null,\"shard\":null,\"index\":\"fruits\",\"clusterAlias\":null,\"sourceAsMap\":{\"color\":null,\"name\":\"pasteque\",\"id\":\"d3782cf5-5be6-4358-91b1-11ce5ef43352\"},\"innerHits\":null,\"fields\":{},\"sourceRef\":{\"fragment\":true},\"rawSortValues\":[],\"sourceAsString\":\"{\\\"id\\\":\\\"d3782cf5-5be6-4358-91b1-11ce5ef43352\\\",\\\"name\\\":\\\"pasteque\\\",\\\"color\\\":null}\",\"fragment\":false},{\"score\":1.6455007,\"id\":\"866bcfa0-53e6-4eb4-8e0f-a07dbfb75e65\",\"type\":\"_doc\",\"nestedIdentity\":null,\"version\":-1,\"seqNo\":-2,\"primaryTerm\":0,\"highlightFields\":{},\"sortValues\":[],\"matchedQueries\":[],\"explanation\":null,\"shard\":null,\"index\":\"fruits\",\"clusterAlias\":null,\"sourceAsMap\":{\"color\":null,\"name\":\"pasteque\",\"description\":\"pepin rouge\",\"id\":\"866bcfa0-53e6-4eb4-8e0f-a07dbfb75e65\"},\"innerHits\":null,\"fields\":{},\"sourceRef\":{\"fragment\":true},\"rawSortValues\":[],\"sourceAsString\":\"{\\\"id\\\":\\\"866bcfa0-53e6-4eb4-8e0f-a07dbfb75e65\\\",\\\"name\\\":\\\"pasteque\\\",\\\"color\\\":null,\\\"description\\\":\\\"pepin rouge\\\"}\",\"fragment\":false},{\"score\":1.6455007,\"id\":\"5cc8ae6e-93ed-49ad-9791-ed5ec4f9601c\",\"type\":\"_doc\",\"nestedIdentity\":null,\"version\":-1,\"seqNo\":-2,\"primaryTerm\":0,\"highlightFields\":{},\"sortValues\":[],\"matchedQueries\":[],\"explanation\":null,\"shard\":null,\"index\":\"fruits\",\"clusterAlias\":null,\"sourceAsMap\":{\"color\":null,\"name\":\"pasteque\",\"description\":\"fraiche\",\"id\":\"5cc8ae6e-93ed-49ad-9791-ed5ec4f9601c\"},\"innerHits\":null,\"fields\":{},\"sourceRef\":{\"fragment\":true},\"rawSortValues\":[],\"sourceAsString\":\"{\\\"id\\\":\\\"5cc8ae6e-93ed-49ad-9791-ed5ec4f9601c\\\",\\\"name\\\":\\\"pasteque\\\",\\\"color\\\":null,\\\"description\\\":\\\"fraiche\\\"}\",\"fragment\":false},{\"score\":1.6455007,\"id\":\"e7593cb8-9e92-43b4-b418-eb70b458ee10\",\"type\":\"_doc\",\"nestedIdentity\":null,\"version\":-1,\"seqNo\":-2,\"primaryTerm\":0,\"highlightFields\":{},\"sortValues\":[],\"matchedQueries\":[],\"explanation\":null,\"shard\":null,\"index\":\"fruits\",\"clusterAlias\":null,\"sourceAsMap\":{\"color\":null,\"name\":\"pasteque\",\"description\":\"rouge et verte\",\"id\":\"e7593cb8-9e92-43b4-b418-eb70b458ee10\"},\"innerHits\":null,\"fields\":{},\"sourceRef\":{\"fragment\":true},\"rawSortValues\":[],\"sourceAsString\":\"{\\\"id\\\":\\\"e7593cb8-9e92-43b4-b418-eb70b458ee10\\\",\\\"name\\\":\\\"pasteque\\\",\\\"color\\\":null,\\\"description\\\":\\\"rouge et verte\\\"}\",\"fragment\":false}],\"totalHits\":{\"value\":4,\"relation\":\"EQUAL_TO\"},\"maxScore\":1.6455007,\"sortFields\":null,\"collapseField\":null,\"collapseValues\":null,\"fragment\":true},\"fragment\":false}";
    }
}