# BasicPJT

### Login Service with Spring Security & Jwt / CRUD with JPA & Querydsl & Pagination

## 1. 프로젝트 개요
- Spring Boot2 지원 종료에 따른 Spring Boot3 학습
- 백엔드 학습을 위한 프로젝트로 프론트 부분은 구현되어 있지 않음
- 회원 기능과 게시글, 댓글, 파일 업로드 기능 구현
- 각 기능들을 REST API 방식으로 구현했으며 Talend API Tester, Swagger를 통해 Test 진행
- Spring Boot 3.2.0 / JDK: 17

## 2. 기술 스택
### Environment

![](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![](https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white)
![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![](https://img.shields.io/badge/Talend-FF6D70?style=for-the-badge&logo=Talend&logoColor=white)


### Development
![](https://img.shields.io/badge/Spring_Boot3-White?style=for-the-badge&logo=SpringBoot&logoColor=white)
![](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)

## 3. REST API 개요
|구분|기능|Method|URL|비고|
|:-:|:-:|:-:|:-:|:-:|
| 회원 API | 로그인 | POST | http://localhost:8080/api/member/login | Spring Security 및 JWT 적용 |
| 회원 API | 로그아웃 | POST | http://localhost:8080/api/member/logout | Spring Security 및 JWT 적용 |
| 회원 API | 회원가입 | POST | http://localhost:8080/api/member/join | 조건을 만족하는 경우에만 회원가입 가능 |
| 게시글 API | 전체 게시글 조회 | GET | http://localhost:8080/api/board/list | Pagination 적용 |
| 게시글 API | 특정 게시글 조회 | GET | http://localhost:8080/api/board/{bno} | - |
| 게시글 API | 게시글 작성 | POST | http://localhost:8080/api/board/register | 로그인 되어있는 경우에만 작성 가능 |
| 게시글 API | 특정 게시글 수정 | PUT | http://localhost:8080/api/board/{bno} | 사용자와 작성자가 동일한 경우에만 작동 |
| 게시글 API | 특정 게시글 삭제 | DELETE | http://localhost:8080/api/board/{bno} | 사용자와 작성자가 동일한 경우에만 작동 |
| 댓글 API | 특정 게시글 댓글 조회 | GET | http://localhost:8080/api/reply/list/{bno} | - |
| 댓글 API | 특정 댓글 조회 | GET | http://localhost:8080/api/reply/{rno} | - |
| 댓글 API | 댓글 작성 | POST | http://localhost:8080/api/reply/register | 로그인 되어있는 경우에만 작성 가능 |
| 댓글 API | 특정 댓글 수정 | PUT | http://localhost:8080/api/reply/{rno} | 사용자와 작성자가 동일한 경우에만 작동 |
| 댓글 API | 특정 댓글 삭제 | DELETE | http://localhost:8080/api/reply/{rno} | 사용자와 작성자가 동일한 경우에만 작동 |
| 파일 API | 파일 업로드 | POST | http://localhost:8080/api/file/upload | 로그인 되어있는 경우에만 업로드 가능 |
| 파일 API | 특정 파일 조회 | GET | http://localhost:8080/api/file/{fileName} | - |
| 파일 API | 특정 파일 삭제 | POST | http://localhost:8080/api/file/{fileName} | - |
| jwtToken API | token generate | POST | http://localhost:8080/generateToken | login api를 통해 호출 |
| jwtToken API | token refresh | POST | http://localhost:8080/refreshToken | AccessToken이 만료된 경우에만 호출 |

## 4. 프로젝트 상세
### 1. Dependencies
```
dependencies {
    // Spring Boot DevTools, Lombok, Spring Web, Spring Security, Spring Data JPA, MariaDB Driver
    // Querydsl, Validation, JAVA EE API, springdoc, Thumbnailator, JWT, Gson, redis
}
```
- DB 관리를 위해 JPA 사용
  - 반복적 CRUD 쿼리 처리
  - 객체 지향적 데이터 관리 가능
- Queryddsl을 사용하여 쿼리 작성 (JAVA 코드로 쿼리 작성)
  - 복잡한 동적 쿼리 처리 용이
  - 문법 오류 검사 용이
  - 쿼리 재사용성 높음
- Validation을 통해 DTO의 요소들의 값 검증
- jdk 8버전 이후 JAVA EE API 모듈이 삭제 되어서 오류 방지 위해 의존성 추가
- Spring Boot3부터 Swagger 사용을 위해 springfox가 아닌 springdoc 사용
- thumbnail 이미지 생성 위해 Thumbnailator 의존성 추가
- JWT Token 사용 위해 JWT 의존성 추가
- JSON 효율적으로 다루기 위해 Gson 의존성 추가
- JWT TOKEN 관리를 위해 redis 의존성 추가

### 2. Security
- 회원가입시 DTO의 각 필드에 부여된 Annotation에 따라 유효한 값인지 검사 후 조건에 맞지 않으면 에러 처리
- 비밀번호 암호화 후 DB에 저장
- SecurityFilterChain을 사용하여 JWT token 발행 및 로그인, 로그아웃 처리
- Login 요청시 Id, Password 검증 후 accessToken(1시간) 생성 후 반환 및 refreshToken(7일) 생성 후 redis 이용해 DB 저장(7일이 경과하면 자동 소멸)
- 만료된 AccessToken인 경우 /refreshToken으로 accesstoken 재발급 요청
- DB에 사용자의 Id의 refreshToken이 있는 경우 accessToken 재발행 및 refreshToken의 남은 기간이 3일 미만이면 refreshToken도 재발행
- Logout 요청시 Id와 관련된 refreshToken을 DB에서 삭제 및 해당 accessToken을 BlackList에 등록시켜 무력화
- 회원가입과 로그인, 게시글과 댓글 GET 요청의 경우를 제외하고는 모두 로그인 되어있어야 접근 가능도록 설정
- 수정과 삭제는 사용자와 작성자의 id가 일치하는 경우에만 가능하도록 설정

### 3. Entity
- Entity와 DTO 분리를 통해 Entity를 불필요한 값 변경으로부터 보호
- board에 reply와 boardImage를 oneTomany로 연결 및 cascade 설정
- boardImage에는 orphanRemoval을 설정하여 연결이 끊어진 이미지의 정보가 DB에 있으면 자동 삭제 되도록 설정
- Lazy Loading을 사용하여 연관된 정보가 사용될 때만 로딩하게 하여 효율적으로 처리하게 설정

### 4. ETC
- Pagination 적용
- CustromRestAdvice를 통해 RestController 에러 처리
- AccessToken과 RefreshToken의 유효성 검사를 통해 해당 token이 올바른지, 만료되었는지 등을 판별 후 해당 Exception을 통한 에러 처리


