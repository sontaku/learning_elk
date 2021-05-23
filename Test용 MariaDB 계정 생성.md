# Test용 MariaDB 계정 생성

cmd에서 진행



## 접속

```mariadb
mariadb -uroot -p

ID : root
pw : root
```



## 사용중인 계정 조회

```mariadb
use mysql;	
select host, user, password from user;
```



## DB 확인

```mariadb
show databases;
```



## DB 생성

```mariadb
DROP DATABASE elktest;
CREATE DATABASE elktest;
```



## 계정 생성

```mariadb
USE elktest;

-- 사용자 확인
SELECT host, user, password FROM user;

-- 사용자 계정 생성
-- CREATE USER '아이디'@'%' IDENTIFIED BY '비밀번호';
-- CREATE USER 계정이름@localhost identified by '비밀번호'; -- 로컬에서만 접속가능
-- CREATE USER 'elkt'@'%' IDENTIFIED BY 'elkt';
DROP USER elkt@localhost;
CREATE USER 'elkt'@localhost IDENTIFIED BY 'elkt';
```



## 권한 부여

```mariadb
-- 사용자 권한 주기
-- GRANT ALL PRIVILEGES ON elktest.* TO 'elkt'@'%';
-- GRANT SELECT ON db스키마.* TO `계정이름`@`%` identified by '비밀번호';
GRANT ALL PRIVILEGES ON elktest.* TO 'elkt'@'localhost';
GRANT SELECT ON elktest.* TO `elkt`@`localhost` identified by 'elkt';

-- 새로고침
FLUSH PRIVILEGES;
```

