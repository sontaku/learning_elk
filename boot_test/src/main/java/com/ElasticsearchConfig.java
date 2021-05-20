package com;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class ElasticsearchConfig {
//
//  @Bean
//  public RestHighLevelClient client(ElasticsearchProperties properties) {
//    return new RestHighLevelClient(
//        RestClient.builder(properties.hosts())
//    );
//  }
//}

//@Configuration
//public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
//
//  @Override
//  @Bean
//  public RestHighLevelClient elasticsearchClient() {
//    // 클러스터 주소를 제공하기 위해 builder를 사용한다. 디폴트 HttpHeaders나 사용가능한 SSL로 셋한다.
//    final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//        .connectedTo("localhost:9200")
//        .build();
//    // RestHighLevelClient를 만든다.
//    return RestClients.create(clientConfiguration).rest();
//  }
//}
