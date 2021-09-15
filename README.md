This is the solution branch containing the outcome after following the below tutorial.

---

Hands-on tutorial based on a demo application that [builds and runs as either Spring Boot or Quarkus](https://developers.redhat.com/blog/2021/02/09/spring-boot-on-quarkus-magic-or-madness).

This tutorial takes a Spring Boot application using Spring MVC, Spring Data JPA, and a PostgreSQL database and converts it to Quarkus with little-to-no source code changes. It uses the [Red Hat Migration Toolkit for Applications](https://developers.redhat.com/products/mta/overview) to analyze the Spring Boot application and offer suggestions for how to migrate it to Quarkus.

The completed solution to this exercise can be found in this repo's `solution` branch. 

# Additional Resources
- [Quarkus for Spring Developers eBook](https://red.ht/quarkus-spring-devs)
- [Quarkus Insights: Quarkus for Spring Developers](https://youtu.be/RvO8MUfc0kA)
- [Why should I choose Quarkus over Spring for my microservices?](https://developers.redhat.com/articles/2021/08/31/why-should-i-choose-quarkus-over-spring-my-microservices)
- [Spring Boot on Quarkus - Magic or Madness?](https://developers.redhat.com/blog/2021/02/09/spring-boot-on-quarkus-magic-or-madness)
- [Evolution of the Quarkus Developer Experience](https://dzone.com/articles/evolution-of-the-quarkus-developer-experience)
- [Red Hat Migration Toolkit for Applications](https://developers.redhat.com/products/mta/overview)

# Local machine requirements
- A Java 11 runtime
- A container runtime (i.e. [Docker](https://www.docker.com/) or [Podman](https://podman.io/))
    - `docker` commands are used throughout this example
- Access to the internet

# Run the application
1. Start the required PostgreSQL database:
   ```
   docker run -it --rm --name tododb -e POSTGRES_USER=todo -e POSTGRES_PASSWORD=todo -e POSTGRES_DB=tododb -p 5432:5432 postgres:13
   ```
2. Run the application:
   ```shell
   ./mvnw clean spring-boot:run
   ```

   You should see the standard Spring Boot Banner:
   ```shell
     .   ____          _            __ _ _
    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
     '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/
    :: Spring Boot ::                (v2.5.4)
   
   INFO 33595 --- [  restartedMain] com.acme.todo.TodoApplication            : Started TodoApplication in 5.073 seconds (JVM running for 5.544)
   ```
3. Open your browser to http://localhost:8080. You should see

   ![Initial Application Screen](images/spring-todo-1.png)

4. Play around with the application a bit. Type a new todo into the text box and press `Enter`. That todo will show up in the list:

   ![Add a new todo](images/spring-todo-2.png)

    1. Click the empty circle next to a todo to complete it, or uncheck it to mark it as incomplete.
    2. Click the `X` to remove a todo.
    3. The `OpenAPI` link at the bottom of the page will open the OpenAPI 3.0 specification for the application.
    4. The `Swagger UI` link opens the embedded [Swagger UI](https://swagger.io/tools/swagger-ui/), which can be used to execute some of the [RESTful endpoints](https://en.wikipedia.org/wiki/Representational_state_transfer) directly.
    5. The `Prometheus Metrics` link leads to the [Prometheus metrics endpoint](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-export-prometheus), which would be scraped intermittently by [Prometheus](https://prometheus.io/).
    6. The `Health Check` link opens the [built-in health check](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-health) exposed by Spring Boot.
5. Go ahead and play around a bit to see it all in action. Use `CTRL-C` in the terminal to stop the application before proceeding.
6. **IMPORTANT!** Also make sure to stop the docker daemon running the PostgreSQL database from step 1 before proceeding.

# Examine the internals
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

# Analyze the application for migration
We are going to use the [Red Hat Migration Toolkit for Applications (MTA)](https://developers.redhat.com/products/mta/overview) to analyze the application. MTA can be run in a number of different ways:
- A [web application](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/web_console_guide/index) running locally or on some remote machine.
- A [command line interface](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/cli_guide/index).
- A [plugin to most major IDEs](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/ide_plugin_guide/index).
- A [Maven plugin](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/maven_plugin_guide/index).

For this exercise we have [pre-built a container image](https://quay.io/repository/edeandrea/mta-cli) that runs the [command line interface](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/5.2/html/cli_guide/index). This approach was chosen to make it easier to run without having to install anything on a local machine.
> **NOTE:** The [`Dockerfile.mta`](Dockerfile.mta) file was used to create the container image being used.

1. On the terminal from the project directory, run the command `docker run -it -v $(pwd):/opt/project quay.io/edeandrea/mta-cli:latest`
   > **IMPORTANT**: This command works "as-is" on *nix/macos platforms.
   > 
   > If you are on Windows command line, you may need to substitute the command with `docker run -it -v %cd%:/opt/project quay.io/edeandrea/mta-cli:latest`.
   >
   > If on Windows PowerShell, you may need to substitute the command with `docker run -it -v ${PWD}:/opt/project quay.io/edeandrea/mta-cli:latest`.
   > 
   > If none of those options work for you, [see here](https://stackoverflow.com/questions/41485217/mount-current-directory-as-a-volume-in-docker-on-windows-10) for more information on obtaining the current working directory for the `-v` option.

2. Once completed you will see something like:
   ```shell
   Report created: /opt/project/mta-report/index.html
              Access it at this URL: file:///opt/project/mta-report/index.html
   ```
3. In your browser, open up the newly-created `mta-report/index.html` page within the project. You should see the **Application List** page:

   ![Application List](images/mta-app-list.png)

4. Click on **project** to move to the **Dashboard** page:

   ![Dashboard](images/mta-project-dashboard.png)

5. Click on the **issues** tab at the top to move to the **Issues** page:

   ![Issues](images/mta-issues.png)

The analysis produced by the Migration Toolkit for Applications contains many links to Quarkus documentation explaining various concepts, like datasource configuration, build tools, etc. Feel free to click around and visit some of these links.

# Correct Issues
Each issue is something that needs to be dealt with to convert the application from Spring to Quarkus. The majority of the issues presented are related to dependencies within the [`pom.xml`](pom.xml). Let's fix all of those issues first.

1. In the **Migration Mandatory** section, find and click on the `Replace the Spring Parent POM with Quarkus BOM` issue. This will expand and explain the issue detail:

   ![Spring POM to Quarkus BOM](images/spring-pom-to-quarkus-bom-desc.png)

2. Clicking on `pom.xml` will bring up a page describing all the necessary changes needed to the project's `pom.xml`.

## `pom.xml`
The first issue is replacing the Spring parent POM with the Quarkus BOM.

> Quarkus does not use a parent POM. Instead, [Quarkus imports a BOM](https://quarkus.io/guides/maven-tooling#build-tool-maven) inside the `<dependencyManagement>` section of the pom

While we're in `pom.xml` we may as well fix all the issues related to it.

1. In your editor/IDE, open [`pom.xml`](pom.xml)
2. Find the `<parent>` section and remove it
3. In the `<properties>` section, add `<quarkus.platform.version>2.2.2.Final</quarkus.platform.version>`
4. After the `<properties>` section but before the `<dependencies>` section, add the following block:
   ```xml
   <dependencyManagement>
     <dependencies>
       <dependency> 
         <groupId>io.quarkus.platform</groupId>
         <artifactId>quarkus-bom</artifactId>
         <version>${quarkus.platform.version}</version>
         <type>pom</type>
         <scope>import</scope>
       </dependency>
     </dependencies>
   </dependencyManagement>
   ```
   
5. The next issue is `Replace the Spring Web artifact with Quarkus 'spring-web' extension`.

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

   and, according to the [Quarkus Spring Web Guide](https://quarkus.io/guides/spring-web), replace it with
   ```xml
   <dependency>
     <groupId>io.quarkus</groupId>
     <artifactId>quarkus-spring-web</artifactId>
   </dependency>
   ```
   
6. The next issue is `Replace the SpringBoot Data JPA artifact with Quarkus 'spring-data-jpa' extension`.

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   ```

   and, according to the [Quarkus JPA Guide](https://quarkus.io/guides/spring-data-jpa), replace it with
   ```xml
   <dependency>
     <groupId>io.quarkus</groupId>
     <artifactId>quarkus-spring-data-jpa</artifactId>
   </dependency>
   ```
   
7. The next issue is `Spring component springdoc-openapi-ui requires investigation`. [SpringDoc OpenAPI](https://springdoc.org/) is a 3rd party open source library that isn't part of Spring itself. Luckily, there is the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui).

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>org.springdoc</groupId>
     <artifactId>springdoc-openapi-ui</artifactId>
     <version>1.5.10</version>
   </dependency>
   ```

   and replace it with
   ```xml
   <dependency>
     <groupId>io.quarkus</groupId>
     <artifactId>quarkus-smallrye-openapi</artifactId>
   </dependency>
   ```
   
8. The next issue is `Replace the Spring Boot Actuator dependency with Quarkus Smallrye Health extension`.
 
   In `pom.xml`, find
    ```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```

    and, according to the [Quarkus - SmallRye Health Guide](https://quarkus.io/guides/smallrye-health), replace it with
    ```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-health</artifactId>
    </dependency>
    ```
   
9. The next issue is `Spring component spring-boot-starter-test requires investigation`.

   In `pom.xml`, find
     ```xml
     <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
     </dependency>
     ```

     and, according to the [Quarkus testing guide](https://quarkus.io/guides/getting-started-testing), replace it with
     ```xml
     <dependency>
       <groupId>io.quarkus</groupId>
       <artifactId>quarkus-junit5</artifactId>
       <scope>test</scope>
     </dependency>
     ```

---

Some issues that weren't caught by the tool but also need to be fixed:
1. The `io.micrometer:micrometer-registry-prometheus` dependency. This needs to be swapped for the [Quarkus Micrometer extension](http://quarkus.io/guides/micrometer).

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>io.micrometer</groupId>
     <artifactId>micrometer-registry-prometheus</artifactId>
   </dependency>
   ```

   and replace it with
   ```xml
   <dependency>
     <groupId>io.quarkus</groupId>
     <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
   </dependency>
   ```
   
2. The `org.postgresql:postgresql` dependency needs to be swapped for the [Quarkus PostgreSQL extension](https://quarkus.io/guides/datasource#jdbc-datasource-2).

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>org.postgresql</groupId>
     <artifactId>postgresql</artifactId>
     <scope>runtime</scope>
   </dependency>
   ```

   and replace it with
   ```xml
   <dependency>
     <groupId>io.quarkus</groupId>
     <artifactId>quarkus-jdbc-postgresql</artifactId>
   </dependency>
   ```
   
3. The `org.springframework.boot:spring-boot-devtools` isn't needed. The [Spring Boot Developer Tools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools) provides features aiming to enhance developer productivity, such as live reload. These features are part of the core of Quarkus.

   In `pom.xml`, find
   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-devtools</artifactId>
     <optional>true</optional>
   </dependency>
   ```

   and remove it

4. The `org.springframework.boot:spring-boot-maven-plugin` needs to be changed so that the application [is built with Quarkus](https://quarkus.io/guides/maven-tooling#build-tool-maven), both for running on the JVM and in native image.

   In `pom.xml`, find
   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-maven-plugin</artifactId>
       </plugin>
     </plugins>
   </build>
   ```

   and replace it with
   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>io.quarkus</groupId>
         <artifactId>quarkus-maven-plugin</artifactId>
         <version>${quarkus.platform.version}</version>
         <extensions>true</extensions>
         <executions>
           <execution>
             <goals>
               <goal>build</goal>
               <goal>generate-code</goal>
               <goal>generate-code-tests</goal>
             </goals>
           </execution>
         </executions>
       </plugin>
       <plugin>
         <artifactId>maven-compiler-plugin</artifactId>
         <version>${compiler-plugin.version}</version>
         <configuration>
           <parameters>true</parameters>
         </configuration>
       </plugin>
       <plugin>
         <artifactId>maven-surefire-plugin</artifactId>
         <version>${surefire-plugin.version}</version>
         <configuration>
           <systemPropertyVariables>
             <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
             <maven.home>${maven.home}</maven.home>
           </systemPropertyVariables>
         </configuration>
       </plugin>
     </plugins>
   </build>
   <profiles>
     <profile>
       <id>native</id>
       <activation>
         <property>
           <name>native</name>
         </property>
       </activation>
       <build>
         <plugins>
           <plugin>
             <artifactId>maven-failsafe-plugin</artifactId>
             <version>${surefire-plugin.version}</version>
             <executions>
               <execution>
                 <goals>
                   <goal>integration-test</goal>
                   <goal>verify</goal>
                 </goals>
                 <configuration>
                   <systemPropertyVariables>
                     <native.image.path>${project.build.directory}/${project.build.finalName}-runner</native.image.path>
                     <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
                     <maven.home>${maven.home}</maven.home>
                   </systemPropertyVariables>
                 </configuration>
               </execution>
             </executions>
           </plugin>
         </plugins>
       </build>
       <properties>
         <quarkus.package.type>native</quarkus.package.type>
       </properties>
     </profile>
   </profiles>
   ```

   > **NOTE:** While this replacement might seem like a lot of XML, it also sets up the application to [build a native image](https://quarkus.io/guides/building-native-image) using the `native` Maven profile.

6. A Spring Boot application also contains a "main" class with the `@SpringBootApplication` annotation. A Quarkus application does not have such a class. There are 2 options that can be taken:
    1. Remove the [`src/main/java/com/acme/todo/TodoApplication.java`](src/main/java/com/acme/todo/TodoApplication.java) class

   **OR**

    2. Add the `org.springframework.boot:spring-boot-autoconfigure` dependency as an `optional` Maven dependency. An `optional` dependency is available when an application compiles but is not packaged with the application at runtime. Doing this would allow the application to compile without modification, but you would also need to maintain a Spring version along with the Quarkus application.
   
       To use this option, add this to the `<dependencies>` section of `pom.xml`:
       ```xml
       <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-autoconfigure</artifactId>
         <version>2.5.4</version>
         <optional>true</optional>
       </dependency>
       ```

       > **NOTE:** This is the option chosen in the `solution` branch of this repository. This option was chosen purely because we did not want to have to change any source code within the project. In a more "real world" scenario, the better option would most likely be option 1.

Now that the changes to `pom.xml` are complete, save and close it.

When completed, your `pom.xml` should look like the [`pom.xml` in the solution branch](https://github.com/RedHat-Middleware-Workshops/spring-to-quarkus-todo/blob/solution/pom.xml).

# Re-analyze application
Now let's re-analyze the application to see how much of the migration has been completed.

1. On the terminal from the project directory, re-run the command `docker run -it -v $(pwd):/opt/project quay.io/edeandrea/mta-cli:latest`
   > **IMPORTANT**: This command works "as-is" on *nix/macos platforms.
   >
   > If you are on Windows command line, you may need to substitute the command with `docker run -it -v %cd%:/opt/project quay.io/edeandrea/mta-cli:latest`.
   >
   > If on Windows PowerShell, you may need to substitute the command with `docker run -it -v ${PWD}:/opt/project quay.io/edeandrea/mta-cli:latest`.
   >
   > If none of those options work for you, [see here](https://stackoverflow.com/questions/41485217/mount-current-directory-as-a-volume-in-docker-on-windows-10) for more information on obtaining the current working directory for the `-v` option.

2. Once completed you will see something like:
   ```shell
   Report created: /opt/project/mta-report/index.html
              Access it at this URL: file:///opt/project/mta-report/index.html
   ```

3. Clicking back to the **Issues** tab should only show a single category of issues: `Replace Spring datasource property key/value pairs with Quarkus properties`.
   ![Issues after pom.xml fixes](images/mta-issues-after-pom-fixes.png)

4. Before proceeding, let's start the newly-converted Quarkus application in [Quarkus's Dev Mode](https://quarkus.io/guides/maven-tooling#dev-mode).
5. In the terminal, run `./mvnw clean quarkus:dev`.
6. The Quarkus application should start up, and you should see the Quarkus banner:
   ```shell
   INFO  [io.qua.dev.pos.dep.PostgresqlDevServicesProcessor] (build-49) Dev Services for PostgreSQL started.
   __  ____  __  _____   ___  __ ____  ______ 
   --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
   -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
   --\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
   INFO  [io.quarkus] (Quarkus Main Thread) spring-to-quarkus-todo 0.0.1-SNAPSHOT on JVM (powered by Quarkus 2.2.2.Final) started in 18.063s. Listening on: http://localhost:8080
   INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
   INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [agroal, cdi, hibernate-orm, hibernate-orm-panache, jdbc-postgresql, micrometer, narayana-jta, resteasy, resteasy-jackson, smallrye-context-propagation, smallrye-health, smallrye-openapi, spring-data-jpa, spring-di, spring-web, swagger-ui]
   ```

   > Notice the line `Dev Services for PostgreSQL started`. [Quarkus Dev Services](https://quarkus.io/guides/dev-services) noticed the PostgreSQL extension on the classpath and started a PostgreSQL container image automatically, while also automatically setting all the configuration properties for the application to communicate with it!

7. Re-open your browser to http://localhost:8080.
8. You'll notice a bunch of exceptions in the console log. This is because we haven't finished converting the application. We still need to migrate some Spring datasource configuration.

# Migrate Data source properties
The other issues relate to properties within `src/main/resources/application.properties`.

1. In your browser tab containing the Migration Toolkit analysis, go back to the **Issues** page and choose the `Replace Spring datasource property key/value pairs with Quarkus properties` issue and then click on the `src/main/resources/application.properties` link.
   ![Replace Spring datasource property key/value pairs with Quarkus properties](images/spring-datasource-properties-desc.png)

2. In your editor/IDE, open [`src/main/resources/application.properties`](src/main/resources/application.properties)
3. The Spring-specific properties in this file need to be changed to their Quarkus equivalents
    1. Find `spring.datasource.url=jdbc:postgresql://localhost:5432/tododb` and remove it completely
       > As you saw, [Quarkus Dev Services](https://quarkus.io/guides/dev-services) will automatically create the database for us and bind it to our application.

    2. Similarly, find and remove `spring.datasource.username=todo` and `spring.datasource.password=todo` as well

---

There are a couple of other properties that the analysis didn't find.
1. Find `spring.jpa.hibernate.ddl-auto=create-drop` and replace with `quarkus.hibernate-orm.database.generation=drop-and-create` according to the [Quarkus Hibernate ORM and JPA Guide](https://quarkus.io/guides/hibernate-orm). This will allow Quarkus (& Hibernate under the covers) to automatically create the database schema upon startup.
2. Add `quarkus.datasource.metrics.enabled=true` so that Quarkus will automatically expose datasource-related metrics to Micrometer.
3. Go back to your browser tab containing the http://localhost:8080 page and refresh it. Magically the page should load and no exceptions in the console!
   > [Quarkus Dev Mode](https://quarkus.io/guides/maven-tooling#dev-mode) has seamlessly redeployed your application while also creating the necessary schema and even importing sample data from [`src/main/resources/import.sql`](src/main/resources/import.sql).

   Interacting with the todo list should now work but the links on the bottom of the page do not. This is because their paths are set to URLs that the UI expects and the Spring version of the application exposed. All Quarkus extensions publish their exposed URIs under the `/q` URI path. Let's reconfigure Quarkus so it uses the same paths that the UI expects.

4. Add `quarkus.swagger-ui.path=/swagger-ui` to map the Swagger UI page from `/q/swagger-ui` to `/swagger-ui`
5. Add `quarkus.micrometer.export.prometheus.path=/actuator/prometheus` to map the Prometheus metrics endpoint from `/q/metrics` to `/actuator/prometheus`
6. Add `quarkus.smallrye-health.root-path=/actuator/health` to map the health endpoint from `/q/health` to `/actuator/health`
7. Add `quarkus.smallrye-openapi.path=/openapi` to map the openapi endpoint from `/q/openapi` to `/openapi`
8. Add `quarkus.swagger-ui.always-include=true` so that Quarkus will always expose the Swagger UI endpoint. By default, it is only exposed in Dev Mode.
9. Save your changes and refresh the browser page. Everything should work as before!
10. Navigate to http://localhost:8080/q/dev to view the [Quarkus Dev UI](https://quarkus.io/guides/dev-ui), a landing page for interacting with your application. All extensions used by the application should show up here along with links to their documentation. Some extensions provide the ability to interact and modify configuration right from the UI.
    > [Continuous testing](https://quarkus.io/guides/continuous-testing) can be enabled and controlled from within the UI. [A video demo is available](https://youtu.be/0JiE-bRt-GU) showcasing continuous testing features.

11. Hit `CTRL-C` in your terminal once done.

# Bonus
As a bonus exercise, let's create and run a Quarkus native image. The easiest way to create a container image containing a native executable is to leverage one of the [Quarkus container-image extensions](https://quarkus.io/guides/building-native-image#using-the-container-image-extensions). If one of those extensions is present, then creating a container image for the native executable is essentially a matter of executing a single command. These extensions also allow us to build a native executable without the need to [install and configure GraalVM](https://quarkus.io/guides/building-native-image#graalvm) on our local machine.

   > **NOTE:** Native image creation is a CPU and memory-intensive operation. It may or may not work depending on your hardware specs. You may need at lease 6 GB of RAM allocated to your Docker daemon.

Since we already have a Docker runtime we'll use the [Docker container image extension](https://quarkus.io/guides/container-image#docker) to perform the container image build.

1. To install the extension into the project, return to the terminal and run `./mvnw quarkus:add-extension -Dextensions="container-image-docker"`
2. Since this is an existing non-Quarkus application, we need to create the `Dockerfile` for the native image.
   > If we had created a new Quarkus application from [Code Quarkus](https://code.quarkus.io), this would have been created for us.

    1. Create the directory `src/main/docker`
    2. Inside `src/main/docker`, create the file `Dockerfile.native`
    3. Paste in the following into `Dockerfile.native`:
       ```dockerfile
       FROM quay.io/quarkus/quarkus-distroless-image:1.0
       COPY target/*-runner /application

       EXPOSE 8080
       USER nonroot

       CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
       ```
      
    4. Save and close `src/main/docker/Dockerfile.native`
   
3. Building a native image can be accomplished by running `./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.group=` in the terminal. Building a native image may take several minutes to complete depending on the specs of your machine and how much CPU/RAM is available.
   > There are many [container image options](https://quarkus.io/guides/container-image#container-image-options) available. The `quarkus.container-image.group=` option removes the `${user.name}` from the final image name. If we did not include this option, the final image would be created as `${user.name}/${quarkus.application.name}:${quarkus.application.version}`. This simply makes it easier to write this tutorial without having to worry about people's usernames!
 
   > **NOTE:** If the native image build fails due to an out of memory error, you may need to increase the memory size of your docker daemon to a minimum of 6GB. 

4. Once the native image build is complete, start the PostgreSQL database container needed by the application:
   ```shell
   docker run -it --rm --name tododb -e POSTGRES_USER=todo -e POSTGRES_PASSWORD=todo -e POSTGRES_DB=tododb -p 5432:5432 postgres:13
   ```
   
   > Quarkus Dev Services is only available in development mode. Running a native executable runs in production mode.

5. Before starting the native image container, we first need to get the internal ip address of the running PostgreSQL DB so that our Quarkus application can connect to it.
    - In another terminal, run `docker inspect tododb | grep IPAddress`. You should see something like
       ```shell
       "SecondaryIPAddresses": null,
       "IPAddress": "172.17.0.2",
               "IPAddress": "172.17.0.2",
       ```
       
       In this example, the ip address is `172.17.0.2`.

6. Now run the native executable image, **making sure to substitute the ip address in the previous step**
   ```shell
   docker run -i --rm -p 8080:8080 -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://172.17.0.2:5432/tododb -e QUARKUS_DATASOURCE_USERNAME=todo -e QUARKUS_DATASOURCE_PASSWORD=todo spring-to-quarkus-todo:0.0.1-SNAPSHOT
   ```
   > Notice the startup time. It should start up in only a few milliseconds!

7. Return to your browser to http://localhost:8080
8. Everything should work as before! No hassle native image generation!
9. Close both the application and the PostgreSQL instances via `CTRL-C` when you're done. 
