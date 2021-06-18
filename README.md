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

아래 경로에서 내려받는다.(.zip)

https://www.elastic.co/kr/downloads/past-releases/elasticsearch-7-12-1



설치 이후 테스트

1. elasticsearch-7.12.1/bin/elasticsearch.bat 배치파일 실행

2. 터미널에서 elasticsearch 서버 값 체크

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

      ```network.host: 0.0.0.0``` 추가 (_따옴표 없음!_)

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

   https://downloads.mysql.com/archives/c-j/

   ```/usr/local/etc/logstash/tools```  밑에 설치 

   tools는 새로 추가한 디렉토리이며, 이름은 중요치 않음

   

2. 설정파일 변경

   테스트 환경에서 사용할 임시테이블 news

   ```mariadb
   CREATE TABLE news(
   	news_id int,
       title VARCHAR(100),
       url VARCHAR(500),
   	category VARCHAR(30),
       currdate DATE
   );
   ```

   

   config/logstash-sample.conf 참조

   위 파일 참조하여 news 테이블 값을 불러 올 news.conf 생성

   ```
   input {
     jdbc {
        jdbc_driver_library => "/usr/local/etc/logstash/tools/mysql-connector-java-5.1.38-bin.jar"
        jdbc_driver_class => "com.mysql.jdbc.Driver"
        jdbc_connection_string => "jdbc:mysql://localhost:3306/elktest"
        jdbc_user => "elkt"
        jdbc_password => "elkt"
        statement => "SELECT * FROM news"
        }
   }
   
   output {
     elasticsearch {
       hosts => ["http://localhost:9200"]
       index => "newstest"
       #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
       #user => "elastic"
       #password => "changeme"
     }
   }
   ```

   jdbc_connection_string : 데이터를 가져올 데이터베이스 주소 및 이름

   jdbc_user : DB user명

   jdbc_password : DB p/w

   statement : 해당 DB에 전송할 쿼리

   .

   .

   index : 설정파일의 index 값이 됨

   

3. logstash-7.12.1/bin 경로에서 실행

   ```
   logstash -f "../config/temp.conf"
   ```

   

   테스트 환경에서는 aws rds db데이터를 참조한다.

   player 테이블 참조로 아래와 같이 설정 후 실행

   ``````
   input {
     jdbc {
        jdbc_driver_library => "/usr/local/etc/logstash/tools/mysql-connector-java-5.1.38-bin.jar"
        jdbc_driver_class => "com.mysql.jdbc.Driver"
        jdbc_connection_string => "jdbc:mysql://earlykross.cuopsz9nr7wp.ap-northeast-2.rds.amazonaws.com:3306/earlykross"
        jdbc_user => "ek"
        jdbc_password => "siattiger"
        statement => "SELECT * FROM player"
        }
   }
   
   output {
     elasticsearch {
       hosts => ["http://localhost:9200"]
       index => "iplayer"
       #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
       #user => "elastic"
       #password => "changeme"
     }
   }
   ``````

   ``` logstash -f "../config/player.conf"```



## 3. kibana

### 설치

elasticsearch와 동일한 버전으로 설치(.zip)

https://www.elastic.co/kr/downloads/past-releases/kibana-7-12-1



kibana.yml 설정 파일의 경우 노터치! 



kibana의 경우 5601번의 포트 값을 통해 실행한다.

위의 elastic search - logstash를 차례로 실행시킨 후

결과 값을 https://localhost:5601 에서 확인 가능하다.



![/image/kb_data.png](/image/kb_data.png)



<hr>

## 구현 중 ELK 에러상황

### 1. Logstash 설정파일(*.conf) 작성시 주의사항

- DB명, USER명, PW가 연결된 경로에 존재하는지 확인
- statement 쿼리 작성시 테이블명, 쿼리이름은 소문자로 작성
- Key => "Value" 구조 따옴표 주의



### 2. LINUX 환경에서 구동시

이미 동작하는 es 서버가 있을 시, 기존 동작하는 서버를 유지한채 새로 접속한 서버는 접속실패하므로 확인 요구됨

es접속확인 : ``` ps -ef | grep elasticsearch```

종료 : ```kill port번호```



### 3. ELK 간 버전 통일 필수
