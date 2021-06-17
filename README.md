# learning_elk

## 개발 환경

- Windows 10 64bit

- jdk : OpenJDK 11 hotSpot Windows 64bit

  https://adoptopenjdk.net/releases.html

- elasticsearch : 7.12.1

  *https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch/7.12.1*
  
  *https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client/7.12.1*
  
- spring-boot : 2.4.6

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

gradle dependency를 주입하거나 아래 경로에서 내려받는다.(.zip)

https://www.elastic.co/kr/downloads/past-releases/elasticsearch-7-12-1



설치 이후 테스트

1. elasticsearch-7.12.1/bin/elasticsearch.bat 배치파일 실행

2. ~~터미널에서 elasticsearch 서버 값 체크~~

   ```
   curl http://localhost:9200
   ```
   
3. 기존 설정으로 웹 화면 구동 확인 ( 9200 port 바인딩 확인 )

   ![/image/es_server_text.png](/image/es_server_test.png)

   

4. 외부 접속 허용

   127.0.0.1:9200

   해당 주소는 외부 접속이 차단되어 있어 변경해주어야 함

   1. config/elasticsearch.yml 실행

   2. Network 영역에 

      ```network.host: 0.0.0.0``` 추가

   3. Discovery 영역

      ```cluster.initial_master_nodes: ["node-1", "node-2"]``` 주석 해제

   4. bat 배치파일 재실행 후 접속 확인

      ![/image/es_server_test_local_ip.png](/image/es_server_test_local_ip.png)

   







## 2. logstash

### 설치

elasticsearch와 동일한 버전으로 설치(.zip)

https://www.elastic.co/kr/downloads/past-releases/logstash-7-12-1



### 테스트

1. jdbc파일 설치

   https://downloads.mariadb.org/connector-java/3.0.0/

   logstash-7.12.1/lib 밑에 설치

2. 설정파일 변경

   config/logstash-sample.conf 참조

   위 파일 복사하여 temp.conf 생성

   ```
   # Sample Logstash configuration for creating a simple
   # Beats -> Logstash -> Elasticsearch pipeline.
   
   input {
     beats {
       port => 5044
     }
     jdbc {
        jdbc_validate_connection => true
        jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
        jdbc_driver_library => "C:/00.siat/00.sw/00.lib/elk/logstash-7.12.1/lib/mysql-connector-java-8.0.25.jar"
        jdbc_connection_string => "jdbc:mariadb://earlykross.cuopsz9nr7wp.ap-northeast-2.rds.amazonaws.com:3306/earlykross"
        jdbc_user => "ek"
        jdbc_password => "siattiger"
        statement => "SELECT * FROM member"
        schedule => "*/1 * * * *"
        }
   }
   
   output {
     elasticsearch {
       hosts => ["http://localhost:9200"]
       index => "member"
       #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
       #user => "elastic"
       #password => "changeme"
     }
   }
   ```

3. logstash-7.12.1/bin 경로에서 실행

   ```
   logstash -f "../config/temp.conf"
   ```

   테스트 환경에서 player 테이블 참조로 아래와 같이 설정 후 실행

   ``````
   # Sample Logstash configuration for creating a simple
   # Beats -> Logstash -> Elasticsearch pipeline.
   
   input {
     beats {
       port => 5044
     }
     jdbc {
        jdbc_validate_connection => true
        jdbc_driver_class => "com.mysql.jdbc.Driver"
        jdbc_driver_library => "로컬경로/jdbc.jar" 파일
        jdbc_connection_string => "jdbc:mariadb://earlykross.cuopsz9nr7wp.ap-northeast-2.rds.amazonaws.com:3306/earlykross"
        jdbc_user => "ek"
        jdbc_password => "siattiger"
        statement => "SELECT * FROM player"
        schedule => "*/1 * * * *"
        }
   }
   
   output {
     elasticsearch {
       hosts => ["http://localhost:9200"]
       index => "player"
       #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
       #user => "elastic"
       #password => "changeme"
     }
   }
   ``````

   ``` logstash -f "../config/player.conf"```



## 3. kibana

