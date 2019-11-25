# A Spring Boot Web Application Seed with tons of Ready to use features

### Intro
This is a simple micro blogging application where you can post a note/blog and other can view it.

The default username/passwords are listed on : g.t.app.Application.initData, which are:

- system/pass
- user1/pass
- user2/pass

Microservice version of this app with several other features is under development at https://github.com/gtiwari333/spring-boot-web-application-seed

### Requirements
- JDK 11+
- Lombok configured on IDE
    - http://ganeshtiwaridotcomdotnp.blogspot.com/2016/03/configuring-lombok-on-intellij.html
    - For eclipse, download the lombok jar, run it, and point to eclipse installation
- Maven (optional)

### How to Run
- Clone/Download and Import project into your IDE, compile and run Application.java 

OR

- mvnw compile spring-boot:run   //if you don't have maven installed in your PC

OR

- mvn compile spring-boot:run //if you have maven  installed in your PC

And open   `http://localhost:8080` on your browser


### Included Features/Samples

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
- Swagger API Docs with UI  ( http://localhost:8080/swagger-ui.html)
- @RestControllerAdvice, @ControllerAdvice demo
- CRUD Note + File upload
- Spring / Maven profiles for dev/prod/docker ...
- Dockerfile to run images
- Docker maven plugin to publish images (follow docker-steps.md)
- Deploy to Amazon EC2 ( follow docker-steps.md )
- Code Generation: lombok,  mapstruct 
- H2 db for local, Console enabled for local ( http://localhost:8080/h2-console/, db url: jdbc:h2:mem:testdb, username: sa)
- MySQL or any other SQL db can be configured for prod/docker etc profiles
- User/User_Authority entity and repository/services
    - login, logout, home pages based on user role
- Security with basic config
- Domain object Access security check on update/delete using custom PermissionEvaluator
- public home page -- view all notes by all 
- private pages based on user roles

Future: do more stuff
- Account management, signup
- Test cases - unit/integration/e2e with fixtures, default data etc
- Email and background job
- Markdown editor?
- elastic search, statistics, dashboard - search into files as well
- rate limit by IP on public API ( note api )
- cleanup/freeze core features and split into microservice architecture ( a separate project )
    - statistics service - dashboard to display popular vs recent - cache the dashboard
    - search service using elastic search -- 
    - note service
    - public API in separate microservice   
    - user mgmt -> using keycloak ( last )
    - Docker compose for deployment of microservices
- Signup using keycloak ?


## Screenshots:

#### Public View
![](screenshots/public-view.png)

#### Logged in Feed View
![](screenshots/logged-in-feed-view.png)

#### Logged in List View
![](screenshots/logged-in-note-list-view.png)

