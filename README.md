# spring-reactor

A first glance at Spring 5 with Web Reactive Framework and Project Reactor 3.0. This application works out of the box thanks to Gradle wrapper. It's a fully functioning application: no bugs present!

# Requirements

* Java 8

# Libraries

* Spring Boot 2.0.0
* Reactor 3.0.3
* Spring Web Reactive 0.1.0

# Changing the test URL

Before building the application, please change the URL in the ```application.yml``` file.

* Propertie name: ```ping.url```
* Propertie value: must be a valid URL

# Build

After cloning this repository, please execute the following command:

```
./gradlew clean build
```

# Running the application

The application can be executed with Gradle or IntelliJ IDEA 2016.2.

# With Gradle

To run with Gradle, execute the following command:

```
./gradlew bootRun
```

# End-points

This application contains tow end-points that can be used for testing purposes.

## Parallelism

### http://localhost:8080/parallelism/20

Once the application is running, use the end-point above to test parallel calls with the Project Reactor.

## Serial

### http://localhost:8080/serial/10

Once the application is running, use the end-point above to test serial calls with the Project Reactor.
