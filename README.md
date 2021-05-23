# learning_elk

## 개발 환경

- jdk : OpenJDK 11 hotSpot Windows 64bit

  https://adoptopenjdk.net/releases.html

- elasticsearch : 7.12.1

  *https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch/7.12.1*
  
  *https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client/7.12.1*
  
- spring-boot : 2.4.5

- gradle : 6.3

  ```
  ./gradlew wrapper --gradle-version 6.3
  ```



<hr>

## 1. elasticsearch

엘라스틱서치 버전 호환 참조

- https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface.versions
- https://github.com/spring-projects/spring-data-elasticsearch/blob/main/src/main/asciidoc/preface.adoc

엘라스틱서치 - JDK 버전 호환 참조

https://www.elastic.co/kr/support/matrix#matrix_jvm



### 설치

gradle dependency를 주입하거나 아래 경로에서 내려받는다.

https://www.elastic.co/kr/downloads/past-releases/elasticsearch-7-12-1



설치 이후 테스트

1. elasticsearch-7.12.1/bin/elasticsearch.bat 배치파일 실행

2. 터미널에서 elasticsearch 서버 값 체크

   ```
   curl http://localhost:9200
   ```





## 2. logstash



## 3. kibana