# A Spring Boot Web Application Seed with tons of Ready to use features. It can be used as starter for BIG projects.

### Variations
- Version without KeyCloak is on 'without-keycloak' branch https://github.com/gtiwari333/spring-boot-web-application-seed/tree/without-keycloak
- Version without KeyCloak and multi-modules is on 'single-module-no-keycloak' branch https://github.com/gtiwari333/spring-boot-web-application-seed/tree/single-module-no-keycloak

## Included Features/Samples
- Modular application
- Data JPA with User/Authority/Note/ReceivedFile entities, example of EntityGraph
- Default test data created while running the app
- Public and internal pages
- MVC with thymeleaf templating
- File upload/download
- Live update of thymeleaf templates for local development
- HTML fragments
- webjar - bootstrap4 + jquery
- Custom Error page
- Request logger filter
- Swagger API Docs with UI  ( http://localhost:8081/swagger-ui.html)
- @RestControllerAdvice, @ControllerAdvice demo
- CRUD Note + File upload
- Spring / Maven profiles for dev/prod/docker ...
- Dockerfile to run images
- Docker maven plugin to publish images (follow docker-steps.md)
- Deploy to Amazon EC2 ( follow docker-steps.md )
- Code Generation: lombok,  mapstruct 
- H2 db for local, Console enabled for local ( http://localhost:8081/h2-console/, db url: jdbc:h2:mem:testdb, username: sa)
- MySQL or any other SQL db can be configured for prod/docker etc profiles
- User/User_Authority entity and repository/services
    - login, logout, home pages based on user role
- Security with basic config
- Domain object Access security check on update/delete using custom PermissionEvaluator
- public home page -- view all notes by all 
- private pages based on user roles
- Test cases - unit/integration with JUnit 5, Mockito and Spring Test
- e2e with Selenide, fixtures. default data generated using Spring
- file upload/download e2e test with Selenide
- Architecture test using ArchUnit
- jOOQ integration with code generation based on JPA entity 
- Account management with KeyCloak
- Testcontainers to perform realistic integration test with KeyCloak 
- favicon handler
- Microservices
- Spring Cloud - Open Feign, Sleuth integration
- Message Queue using ActiveMQ Artemis
- MySQL using testcontainer

Future: do more stuff
- Spring Cloud Contract integration
- Example of background jobs with Quartz with a basic API/UI
- Liquibase/Flyway DB change log
- Integrate Markdown editor for writing notes
- search service using elastic search -- search into uploaded files as well
    - WIP
- rate limit by IP on public API ( article api )
- Fetch user's avatar
- UI improvement
- S3 file upload, test with localstack testcontainer
- approval/flagging api - message based

 
### Requirements
- JDK 11+
- Lombok configured on IDE
    - http://ganeshtiwaridotcomdotnp.blogspot.com/2016/03/configuring-lombok-on-intellij.html
    - For eclipse, download the lombok jar, run it, and point to eclipse installation
- Maven
- Docker

### How to Run

It contains following applications:

- main-app
- email-service (optional)
- report-service (optional)
- trend-service (optional)

Option 1 - run with manually started KeyCloak, ElasticSearch and ActiveMQ servers
- Run ```mvn clean install``` at root 
- Run ```docker-compose -f _config/docker-compose.yml up``` at root to start docker containers
- Go to main-app folder and run ```mvn``` to start the application

Option 2 - automatically start KeyCloak, ElasticSearch and ActiveMQ using TestContainer while application is starting
- Run ```mvn clean install``` at root 
- Go to main-app folder and run ```mvn -Pdev,withTestContainer``` to start the application

Option 3 - import into your IDE and compile the full project and run the Application.java on main-app module

Once the application starts, open  `http://localhost:8081` on your browser. The default username/passwords are listed on : gt.app.Application.initData, which are:

- system/pass
- user1/pass
- user2/pass

## Screenshots:

#### Public View
![](screenshots/public-view.png)

#### Logged in Feed View
![](screenshots/logged-in-feed-view.png)

#### Logged in List View
![](screenshots/logged-in-note-list-view.png)
