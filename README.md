# Spring Boot application template

[![Build Status](https://travis-ci.org/hmcts/spring-boot-template.svg?branch=master)](https://travis-ci.org/hmcts/spring-boot-template)

[![codecov](https://codecov.io/gh/hmcts/probate-sol-ccd-service/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/probate-sol-ccd-service)

## Purpose

The purpose of this template is to speed up the creation of new Spring applications within HMCTS
and help keep the same standards across multpile teams. If you need to create a new app, you can
simply use this one as a starting point and build on top of it.

## What's inside

The template is a working application with a minimal setup. It contains:
 * application skeleton
 * common plugins and libraries
 * docker setup
 * code quality tools already set up
 * integration with Travis CI
 * Hystrix circuit breaker enabled
 * Hystrix dashboard
 * MIT license and contribution information

The application exposes health endpoint (http://localhost:4550/health) and metrics endpoint
(http://localhost:4550/metrics).

## Plugins

The template contains the following plugins:

  * checkstyle

    https://docs.gradle.org/current/userguide/checkstyle_plugin.html

    Performs quality checks on Java source files using Checkstyle and generates reports from these checks.
    The checks are included in gradle's *check* task (you can run them by executing `gradle check` command).

  * jacoco

    https://docs.gradle.org/current/userguide/jacoco_plugin.html

    Provides code coverage metrics for Java code via integration with JaCoCo.
    You can create the report by running the following command:

    ```bash
      ./gradlew jacocoTestReport
    ```

    The report will be created in build/reports subdirectory in your project directory.

  * io.spring.dependency-management

    https://github.com/spring-gradle-plugins/dependency-management-plugin

    Provides Maven-like dependency management. Allows you to declare dependency management
    using `dependency 'groupId:artifactId:version'`
    or `dependency group:'group', name:'name', version:version'`.

  * org.springframework.boot

    http://projects.spring.io/spring-boot/

    Reduces the amount of work needed to create a Spring application

  * org.owasp.dependencycheck

    https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html

    Provides monitoring of the project's dependent libraries and creating a report
    of known vulnerable components that are included in the build. To run it
    execute `gradle dependencyCheck` command.

  * com.github.ben-manes.versions

    https://github.com/ben-manes/gradle-versions-plugin

    Provides a task to determine which dependencies have updates. Usage:

    ```bash
      ./gradlew dependencyUpdates -Drevision=release
    ```

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew installDist
```

Create docker image:

```bash
  docker-compose build
```

Run the distribution (created in `build/install/spring-boot-template` directory)
by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port
(set to `4550` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:4550/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

## Setting up the ccd with roles locally

Follow these instructions to setup data on ccd for Sols - using the latest definition (xls)

### Host change in definition file
make a note of your local ip address using ifconfig (for me it was labelled tun0)
change the endpoint host on any sols callbacks to use this ip address in the definition

### Start up
launch the all the containers
```bash
docker-compose -f docker/docker-compose-with-ccd.yml up
```

### Roles
upload needed roles
```bash
./bin/ccd-add-all-roles.sh
```

### Import
Use the latest version of the spreadsheet from 
https://git.reform.hmcts.net/probate/ccd-import-spreadsheet
#### Pick the correct one for the current ccd version you are working on
import the amended definition
```bash
./bin/ccd-import-definition.sh "../CCD_Probate_V11.1-Dev.xlsx"
```
Make sure you update the caseEvents tab, cell S1 to be local and update the ip address in T1  
The copy the fields as instructed on the next row of the spreadsheet  
Also update the PrintableDocumentsUrl in the caseType tab on the spreadsheet in the same manner

### Go to ccd web app
```
http://localhost:3451/
```

login to ccd
For a solicitor use ProbateSolicitor1@gmail.com : password  
Alternatively, for a caseworker use  
ProbateSolCW1@gmail.com : password

### Notes:
You may need to forcibly remove any relevant images db first to ensure the db init is correctly completed
```bash
docker image rmi -f $(docker image ls -a -q)
```
You may also find removing all ontainers helps
```bash
docker container rm -f $(docker container ls -a -q)
```

When the containers are restarted, ccd data has to be reloaded
The user token expires approx every 4 hours

### Mac users
You will need to reconfigure your docker settings to allow at least 6.5Gb

## Hystrix

[Hystrix](https://github.com/Netflix/Hystrix/wiki) is a library that helps you control the interactions
between your application and other services by adding latency tolerance and fault tolerance logic. It does this
by isolating points of access between the services, stopping cascading failures across them,
and providing fallback options. We recommend you to use Hystrix in your application if it calls any services.

### Hystrix circuit breaker

This template API has [Hystrix Circuit Breaker](https://github.com/Netflix/Hystrix/wiki/How-it-Works#circuit-breaker)
already enabled. It monitors and manages all the`@HystrixCommand` or `HystrixObservableCommand` annotated methods
inside `@Component` or `@Service` annotated classes.

### Hystrix dashboard

When this API is running, you can monitor Hystrix metrics in real time using
[Hystrix Dashboard](https://github.com/Netflix/Hystrix/wiki/Dashboard).
In order to do this, visit `http://localhost:4550/hystrix` and provide `http://localhost:4550/hystrix.stream`
as the Hystrix event stream URL. Keep in mind that you'll only see data once some
of your Hystrix commands have been executed. Otherwise `Loading ...` message will be displayed
on the monitoring page.

### Other

Hystrix offers much more than Circuit Breaker pattern implementation or command monitoring.
Here are some other functionalities it provides:
 * [Separate, per-dependency thread pools](https://github.com/Netflix/Hystrix/wiki/How-it-Works#isolation)
 * [Semaphores](https://github.com/Netflix/Hystrix/wiki/How-it-Works#semaphores), which you can use to limit
 the number of concurrent calls to any given dependency
 * [Request caching](https://github.com/Netflix/Hystrix/wiki/How-it-Works#request-caching), allowing
 different code paths to execute Hystrix Commands without worrying about duplicating work

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details
