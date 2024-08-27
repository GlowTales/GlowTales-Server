# GlowTales-Server
애자일 팀의 글로우테일 서비스 BE 레포입니다

![IMG_3425.JPG](..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2FIMG_3425.JPG)

<br>

## GlowTales
<aside>
팀 애자일은 청각장애인, 그리고 다문화 가정에서의 문제점을 찾았습니다. <br/>

말이 어눌한 청각장애인 부모나 자녀와 구사하는 언어가 다른 다문화 가정의 부모님들은 동화책을 생생하게 읽어주며 언어를 함께 학습하는 것에 대한 어려움을 겪고 있습니다. 이러한 문제는 부모와 자녀 간의 정서적 유대감을 약화시키고, 아이들의 언어 발달에도 부정적인 영향을 미치고 있습니다.<br/>

저희는 이 문제를 해결하기 위해 사진, 텍스트 등 일상에서 떠오른 것을 입력하면 다양한 언어로 Ai가 동화를 생성해주는 서비스를 제작하고자 합니다. 동화 생성에서 더 나아가 이를 기반으로 언어 학습까지 연계할 수 있습니다.<br/>

이 서비스는 청각장애인, 다문화 가정에서의 문제를 해결하고, 모든 아이들이 평등하게 언어 학습을 할 수 있도록 돕는 중요한 도구가 될 것 입니다.<br/>
</aside>

<br/><br/>

## Contributors ✨

<div align=center>

| | <img width="30" src="https://avatars.githubusercontent.com/u/101704889?v=4">[@eunsu02](https://github.com/eunsu02) | <img width="30" src="https://avatars.githubusercontent.com/u/96684524?v=4">[@hayeongKo](https://github.com/hayeongKo) | 
|-------|--------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| 역할 | 백엔드 개발                                                                                                             | 백엔드 개발                                                                                                                |
| 담당 API | <li>CICD 세팅</li><li>객체 검출 API</li><li>학습 완료/미완료 동화 조회 API</li><li>퀴즈/정답 조회 API</li><li>사용자 정보 조회 API</li>          | <li>ChatGPT 프롬프트 엔지니어링</li><li>카카오 로그인 구현</li><li>동화 생성 API</li><li>퀴즈 생성 API</li> <li>퀴즈 정답 갱신 요청 API</li>           |

</div>
<hr></hr>

## API 명세서

<br>
<a href="https://glow-tales.p-e.kr/swagger-ui/index.html#" target="_blank">GlowTales-Server API 명세서 바로가기</a>

## Architecture

<br>

<hr></hr>

## 📁 폴더 구조

```
└── GlowTales-Server
    ├── gradle
    │   └── wrapper
    ├── scripts
    └── src
        ├── main
        │   ├── java
        │   │   └── com
        │   │       └── example
        │   │           └── glowtales
        │   │               ├── config
        │   │               │   ├── auth
        │   │               │   ├── jwt
        │   │               │   └── oauth
        │   │               ├── controller
        │   │               ├── converter
        │   │               ├── domain
        │   │               ├── dto
        │   │               │   ├── jwt
        │   │               │   ├── language
        │   │               │   ├── request
        │   │               │   └── response
        │   │               │       ├── auth
        │   │               │       ├── member
        │   │               │       ├── quiz
        │   │               │       └── tale
        │   │               ├── repository
        │   │               └── service
        │   │                   └── oauth
        │   └── resources
        │       └── templates
        └── test
            └── java
                └── com
                    └── example
                        └── glowtales
```

<hr></hr>

## ERD
![GlowTales.png](..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2FGlowTales.png)

<hr></hr>

## Branch Convention

- `main` : 프로덕트를 배포하는 브랜치입니다.
- `dev`: 프로덕트 배포 전 기능을 개발하는 브랜치입니다.
- `feat`: 단위 기능을 개발하는 브랜치로 단위 기능 개발이 완료되면 develop 브랜치에 merge 합니다.
- `hotfix`: main 브랜치로 프로덕트가 배포 된 이후 이슈가 발생했을 때 이를 긴급하게 해결하는 브랜치입니다.

<hr></hr>

## Commit Convetion
- **feat** : 새로운 기능 구현 `[feat] 구글 로그인 API 기능 구현 - #11`
- **fix** : 코드 오류 수정 `[fix] 회원가입 비즈니스 로직 오류 수정 (#10)`
- **etc** : 불필요한 코드 삭제, 문서 개정 `[etc] 불필요한 import 제거 (#12)`
- **refactor** : 내부 로직은 변경 하지 않고 기존의 코드를 개선하는 리팩터링 `[refactor] 코드 로직 개선 (#15)`
- **chore** : 의존성 추가, yml 추가와 수정, 패키지 구조 변경, 파일 이동 등의 작업 `[chore] yml 수정 (#21)`, `chore: lombok 의존성 추가 (#22)`
- **test**: 테스트 코드 작성, 수정 `[test] 로그인 API 테스트 코드 작성 (#20)`
- **setting**: 세팅
- **merge**: 머지

<br>


## Teck Stack ✨

| IDE | IntelliJ                                        |
|:---|:------------------------------------------------|
| Language | Java 17                                         |
| Framework | Spring Boot 3.3.1, Gradle                       |
| Authentication | Spring Security, JSON Web Tokens                |
| Orm | Spring Data JPA                                 |
| Database | MySQL                                           |
| External | AWS EC2, AWS RDS, Docker, Docker-Compose|
| CI/CD | Github Action                                   |
| API Docs | Swagger                                 |
| Other Tool | Discord, Postman, Figma                         |

<br>