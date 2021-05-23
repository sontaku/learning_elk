package elk;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class ElasticSearchTestController {

  @Autowired
  RestHighLevelClient client;

  @Bean(destroyMethod = "close")
  @Scope("prototype")
  public RestHighLevelClient restHighLevelClient() {
    return new RestHighLevelClient(
        RestClient.builder(new HttpHost("192.168.56.101", 9200, "http")));
  }

  @GetMapping(value = "/create")
  public String ping() throws IOException {
    CreateIndexRequest request = new CreateIndexRequest("users3");
    request.settings(Settings.builder()
        .put("index.number_of_shards", 1)
        .put("index.number_of_replicas", 2)
    );
    Map<String, Object> message = new HashMap<>();
    message.put("type", "text");
    Map<String, Object> properties = new HashMap<>();
    properties.put("userId", message);
    properties.put("name", message);
    properties.put("name2", message);
    Map<String, Object> mapping = new HashMap<>();
    mapping.put("properties", properties);
    request.mapping(mapping);
    CreateIndexResponse indexResponse = client.indices().create(request, RequestOptions.DEFAULT);
    return "created";
  }


  //index에 데이터 추가
  @GetMapping(value = "/upsert")
  public String upsert() throws IOException {
    IndexRequest request = new IndexRequest("users3");
    Map<String, Object> users = new HashMap<>();
    users.put("id", "003");
    users.put("name", "윤기용");
    users.put("name2", "윤기용2");
    users.put("age", 35);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
    System.out.println("response id: " + indexResponse.getId());

    users = new HashMap<>();
    users.put("id", "002");
    users.put("name", "윤기돌");
    users.put("name2", "윤기용");
    users.put("age", 43);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    indexResponse = client.index(request, RequestOptions.DEFAULT);

    users = new HashMap<>();
    users.put("id", "004");
    users.put("name", "윤미라");
    users.put("name2", "윤미라2");
    users.put("age", 27);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    indexResponse = client.index(request, RequestOptions.DEFAULT);

    users = new HashMap<>();
    users.put("id", "005");
    users.put("name", "문채원");
    users.put("name2", "문채원짱");
    users.put("age", 2);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    indexResponse = client.index(request, RequestOptions.DEFAULT);

    users = new HashMap<>();
    users.put("id", "006");
    users.put("name", "윤기용이");
    users.put("name2", "윤기용이2");
    users.put("age", 23);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    indexResponse = client.index(request, RequestOptions.DEFAULT);

    users = new HashMap<>();
    users.put("id", "007");
    users.put("name", "윤기용바보");
    users.put("name2", "윤기용바보2");
    users.put("age", 33);
    request.id(users.get("id").toString());
    request.source(new ObjectMapper().writeValueAsString(users), XContentType.JSON);
    indexResponse = client.index(request, RequestOptions.DEFAULT);
    return "success";
  }

  // 데이터 삭제
  @GetMapping(value = "delete")
  public String delete() throws IOException {
    DeleteRequest request = new DeleteRequest("users", "001");
    DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
    return deleteResponse.getId();
  }

  //타입 추가 update
  @GetMapping(value = "update")
  public UpdateResponse update() throws IOException {
    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("reason", "daily update");
    UpdateRequest request = new UpdateRequest("users", "002")
        .doc(jsonMap);

    UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
    return updateResponse;
  }

  //    //데이터 얻어오기
  @GetMapping(value = "get")
  public GetResponse get() throws IOException {

    GetRequest request = new GetRequest(
        "users",
        "002");

    GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
    return getResponse;
  }

  //
//
//	//fuzzyQuery 유사 키워드
//	//검색어와 필드 값이 조금 차이가 나더라도 매치가 되도록 하고 싶을 수 있다. fuzziness 옵션을 지정하면 이것이 가능하다.
  @GetMapping(value = "search101")
  public SearchResponse search101() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.fuzzyQuery("title", "금융");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("news-2021.02.14");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }
//

  @GetMapping(value = "search102")
  public SearchResponse search102() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("name", "윤기용");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("users");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }
//

  @GetMapping(value = "search103")
  public SearchResponse search103() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("윤기용", "name", "name2");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("users3");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search104")
  public SearchResponse search104() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("윤기용", "name", "name2");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("users3");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }


  @GetMapping(value = "search1")
  public SearchResponse search1() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "금융");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("news-2021.02.14");
    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }


  @GetMapping(value = "search2")
  public SearchResponse search2() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.matchQuery("title", "금융"))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC));

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("news-2021.02.14");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }


  @GetMapping(value = "search3")
  public SearchResponse search3() throws IOException {
    QueryBuilder matchQueryBuilder = QueryBuilders.matchAllQuery();

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);

    SearchRequest searchRequest = new SearchRequest("users");
    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  //
  @GetMapping(value = "search4")
  public SearchResponse search4() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.termQuery("name", "윤기용"))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC));

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("users");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search5")
  public SearchResponse search5() throws IOException {
    MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", "윤기용");
    matchQueryBuilder
        .fuzziness(Fuzziness.AUTO)
        .prefixLength(3)
        .maxExpansions(10);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(matchQueryBuilder);
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("users");
    searchRequest.source(sourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

    return searchResponse;
  }

  //
  @GetMapping(value = "search6")
  public SearchResponse search6() throws IOException {

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    AvgAggregationBuilder aggregation = AggregationBuilders.avg("avg_age").field("age");
    SumAggregationBuilder aggregation2 = AggregationBuilders.sum("sum_age").field("age");

    searchSourceBuilder.aggregation(aggregation);
    searchSourceBuilder.aggregation(aggregation2);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("users");
    searchRequest.source(searchSourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

    return searchResponse;
  }


  @GetMapping(value = "search7")
  public SearchResponse search7() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.matchQuery("name", "윤기용"))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC))
        .fetchSource(false);
    AvgAggregationBuilder aggregation = AggregationBuilders.avg("avg_age").field("age");
    SumAggregationBuilder aggregation2 = AggregationBuilders.sum("sum_age").field("age");
    sourceBuilder.aggregation(aggregation);
    sourceBuilder.aggregation(aggregation2);
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("users");
    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search8")
  public SearchResponse search8() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.matchQuery("name", "윤기용"))
        .postFilter(QueryBuilders.rangeQuery("age").from(35).to(null))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC));
    SearchRequest searchRequest = new SearchRequest()
        .indices("users")
        .source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search9")
  public SearchResponse search9() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.matchAllQuery())
        .postFilter(QueryBuilders.rangeQuery("age").gte(35))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC));
    SearchRequest searchRequest = new SearchRequest()
        .indices("users")
        .source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search10")
  public SearchResponse search10() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.matchAllQuery())
        .postFilter(QueryBuilders.rangeQuery("age").lt(35))
        .from(0)
        .size(100)
        .timeout(new TimeValue(3, TimeUnit.MINUTES))
        .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
        .sort(new ScoreSortBuilder().order(SortOrder.DESC));

    SearchRequest searchRequest = new SearchRequest()
        .indices("users")
        .source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search11")
  public SearchResponse search11() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

    MatchQueryBuilder A = QueryBuilders.matchQuery("name", "윤기용");
    MatchQueryBuilder B = QueryBuilders.matchQuery("name", "윤미라");
    MatchQueryBuilder C = QueryBuilders.matchQuery("name", "윤기돌");

    RangeQueryBuilder D = QueryBuilders.rangeQuery("age").lte(2);

    //1.(A AND B AND C)
    BoolQueryBuilder query1 = QueryBuilders.boolQuery();
    query1.must(A).must(B).must(C);

    //2.(A OR B OR C)
    BoolQueryBuilder query2 = QueryBuilders.boolQuery();
    query2.should(A).should(B).should(C);

    //2.(A AND C)
    BoolQueryBuilder query3 = QueryBuilders.boolQuery();
    query3.must(A).must(C).must(D);

    //Compound
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    boolQueryBuilder.must(query2).should(query3);
    sourceBuilder.query(query3);
    SearchRequest searchRequest = new SearchRequest()
        .indices("users")
        .source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    return searchResponse;
  }

  @GetMapping(value = "search12")
  public BulkByScrollResponse search12() throws IOException {
    UpdateByQueryRequest request = new UpdateByQueryRequest("users");
    request.setQuery(new MatchQueryBuilder("_id", "006"));
    request.setScript(
        new Script(
            ScriptType.INLINE, "painless",
            "ctx._source.full_name = '윤기용으윽'",
            Collections.emptyMap()));
    request.setRefresh(true);
    BulkByScrollResponse bulkResponse = client.updateByQuery(request, RequestOptions.DEFAULT);
    return bulkResponse;
  }
}