Hands-on tutorial based on a demo application that [builds and runs as either Spring Boot or Quarkus](https://developers.redhat.com/blog/2021/02/09/spring-boot-on-quarkus-magic-or-madness).

## Run the application
1. Start the required PostgreSQL database:
   ```
   docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name tododb -e POSTGRES_USER=todo -e POSTGRES_PASSWORD=todo -e POSTGRES_DB=tododb -p 5432:5432 postgres:13
   ```
2. Run the application:
   ```
   ./mvnw clean spring-boot:run
   ```
   
   You should see the standard Spring Boot Banner:
   ```
     .   ____          _            __ _ _
    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
     '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/
    :: Spring Boot ::                (v2.5.4)
   
   INFO 33595 --- [  restartedMain] com.acme.todo.TodoApplication            : Started TodoApplication in 5.073 seconds (JVM running for 5.544)
   ```
3. Open browser to http://localhost:8080. You should see
![Initial Application Screen](images/spring-todo-1.png)
4. Play around with the application a bit. Type a new todo into the text box and press `Enter`. That todo will show up in the list
![Add a new todo](images/spring-todo-2.png)
   1. Click the empty circle next to a todo to complete it, or uncheck it to mark it as incomplete.
   2. Click the `X` to remove a todo.
   3. The `OpenAPI` link at the bottom of the page will open the OpenAPI 3.0 specification for the application.
   4. The `Swagger UI` link opens the embedded [Swagger UI](https://swagger.io/tools/swagger-ui/), which can be used to execute some of the [RESTful endpoints](https://en.wikipedia.org/wiki/Representational_state_transfer) directly.
   5. The `Prometheus Metrics` link leads to the [Prometheus metrics endpoint](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-export-prometheus), which would be scraped intermittently by [Prometheus](https://prometheus.io/).
   6. The `Health Check` link opens the [built-in health check](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-health) exposed by Spring Boot.
5. Go ahead and play around a bit to see it all in action. Use `CTRL-C` in the terminal to stop the application before proceeding.

## Examine the internals
- [Spring MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) for building the REST layer:
    - Open [`src/main/java/com/acme/todo/rest/TodoController.java`](src/main/java/com/acme/todo/rest/TodoController.java) to find the Spring MVC RESTful controller, exposing the various endpoints available to the user interface.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html) for defining relational entities as well as storing and retrieving them:
    - Open [`src/main/java/com/acme/todo/domain/TodoEntity.java`](src/main/java/com/acme/todo/domain/TodoEntity.java) to find the [Java Persistence API (JPA)](https://www.oracle.com/java/technologies/persistence-jsp.html), representing the relational table for storing the todos.
    - Open [`src/main/java/com/acme/todo/repository/TodoRepository.java`](src/main/java/com/acme/todo/repository/TodoRepository.java) to find the [Spring Data JPA Repository](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories), exposing all of the create, read, update, and delete operations for the `TodoEntity`.
- [Spring Boot Actuators](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html) for providing operational capabilities, including health checks and metrics gathering.
- [SpringDoc OpenAPI 3](https://springdoc.org/) for generating and exposing RESTful API information as well as the embedded Swagger UI endpoint.
   > **NOTE:** Spring Boot on its own does not have a starter providing this capability.
- [Prometheus Micrometer Registry](https://micrometer.io/docs/registry/prometheus) for exposing metrics to Prometheus.
- Open [`src/main/resources/META-INF/resources`](src/main/resources/META-INF/resources) to find the user interface components used.
- Open [`src/main/resources/application.properties`](src/main/resources/application.properties) to find the application configuration.
- Open [`src/main/resources/import.sql`](src/main/resources/import.sql) to find some SQL that will pre-populate the database table with an initial set of data.

## Analyze the application for migration
We are going to use the [Red Hat Migration Toolkit for Applications (MTA)](https://developers.redhat.com/products/mta/overview) to analyze the application. MTA can be run in a number of different ways:
- A [web application](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/web_console_guide/index) running locally or on some remote machine.
- A [command line interface](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/cli_guide/index).
- A [plugin to most major IDEs](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/ide_plugin_guide/index).
- A [Maven plugin](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/maven_plugin_guide/index).

For this exercise we are going to use the [Maven plugin](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html-single/maven_plugin_guide/index#getting_started) approach because it does not require downloading nor installing anything on the local machine.

1. Install the Maven plugin into the current project:
    1. Open [`pom.xml`](pom.xml)
    2. Find the `<plugins>` block inside the `<build>` block (around line 68).
    3. Add the following `<plugin>` after the `spring-boot-maven-plugin`'s `<plugin>` block:
       ```xml
       <plugin>
         <groupId>org.jboss.windup.plugin</groupId>
         <artifactId>windup-maven-plugin</artifactId>
         <version>5.2.0.Final</version>
         <executions>
           <execution>
             <id>run-windup</id>
             <phase>package</phase>
             <goals>
               <goal>windup</goal>
             </goals>
           </execution>
         </executions>
         <configuration>
           <offlineMode>true</offlineMode>
         </configuration>
       </plugin>
       ```
