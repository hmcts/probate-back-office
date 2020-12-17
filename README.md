w # Spring Boot application template

[![Build Status](https://travis-ci.org/hmcts/spring-boot-template.svg?branch=master)](https://travis-ci.org/hmcts/spring-boot-template)

[![codecov](https://codecov.io/gh/hmcts/probate-back-office/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/probate-back-office)

## Purpose

The purpose of this template is to speed up the creation of new Spring applications within HMCTS
and help keep the same standards across multiple teams. If you need to create a new app, you can
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

### Docker environment

Because the probate back office relies on CCD callbacks it must be run inside the docker-compose environment, and must be built before bringing the environment up. You will need to recompile after any code changes.

Build the jar with:

```
./gradlew assemble
docker-compose build
```

Bring up the environment: 

```
# build the jar
./gradlew assemble

# first time only
npx @hmcts/probate-dev-env --create

# spin up the docker containers
npx @hmcts/probate-dev-env

# use local probate backoffice
docker-compose stop probate-back-office
./gradlew assemble
docker-compose up -d --build probate-back-office
```

If you would like to test a new CCD config locally, you should run:

```
./ccdImports/conversionScripts/createAllXLS.sh probate-back-office:4104
./ccdImports/conversionScripts/importAllXLS.sh
```

To run the back-office app using gradle with environment:

```bash
# set CCD_DM_DOMAIN env var when spinning up containers
CCD_DM_DOMAIN="http://localhost:5006" npx @hmcts/probate-dev-env 

# stop back-office container
docker-compose stop probate-back-office

# generate and import spreadsheets
# for mac users:
./ccdImports/conversionScripts/createAllXLS.sh docker.for.mac.localhost:4104

# for linux users:
./ccdImports/conversionScripts/createAllXLS.sh probate-back-office:4104 

# import xls
./ccdImports/conversionScripts/importAllXLS.sh

# gradle build and run app
./gradlew build
./gradlew bootRun
```
 
##### Probate-back-office
Login to ccd on `http://localhost:3451`. Caseworker: `ProbateSolCW1@gmail.com / Pa55word11`. Solicitor  `ProbateSolicitor1@gmail.com / Pa55word11`.

Start logstash-probateman (for legacy cases)
```bash
   sudo /usr/share/logstash/bin/logstash -f logstash/legacy-case-data-local.conf
```

##### local document-store 
You can go to a doc in dm by going to `localhost:3453/documents/[**ID**]/binary `.

## Linking to a Probate-frontend PR
You must link a probate-frontend pr to a probate-orchestrator pr and that to your probate-backoffice pr
* Create a PR off master for probate-orchestrator-service
* Use the PR number of the BO build in values.yml. Replace:
```
BACK_OFFICE_API_URL: "http://probate-back-office-pr-1101.service.core-compute-preview.internal"
```
* upgrade the Chart.yaml version in probate-orchestrator-service
```
version: 1.0.1
```
* Create a PR off master for probate-frontend
* Use the PR number of the Orchestrator build in values.yml. Replace:
```
ORCHESTRATOR_SERVICE_URL : http://probate-orchestrator-service-pr-334.service.core-compute-preview.internal
```
* upgrade the Chart.yaml version in probate-frontend
```
version: 2.0.14
```
* Build the 2 PRs above
* For probate-frontend access, go to (use the pr number you just created for fe):
```
https://probate-frontend-pr-1218.service.core-compute-preview.internal/start-eligibility
```
##### VPN and proxy will be needed to access this

#####REMEMBER
######Remove your unwanted FE/ORCH PRs when you have finished QA

## Linking to a Caveats Frontend PR
Exactly the same as above, except you need to link on the probate-caveats-frontend PR
* Create a PR off master for probate-orchestrator-service
* Use the PR number of the BO build in values.yml. Replace:
```
BACK_OFFICE_API_URL: "http://probate-back-office-pr-1101.service.core-compute-preview.internal"
```
* upgrade the Chart.yaml version in probate-orchestrator-service
```
version: 1.0.1
```
* Create a PR off master for probate-caveats-frontend
* Use the PR number of the Orchestrator build in values.yml. Replace:
```
ORCHESTRATOR_SERVICE_URL : http://probate-orchestrator-service-pr-334.service.core-compute-preview.internal
```
* upgrade the Chart.yaml version in probate-caveats-frontend
```
version: 2.0.14
```
* Build the 2 PRs above
* For probate-caveats-frontend access, go to (use the pr number you just created for fe):
```
https://probate-caveats-fe-pr-276.service.core-compute-preview.internal/caveats/start-apply
```
##### VPN and proxy will be needed to access this

## PR Health urls
### example PR urls 
replace pr-{NUMBER} as appropriate 
```
https://probate-frontend-pr-1218.service.core-compute-preview.internal/health
https://probate-caveats-fe-pr-276.service.core-compute-preview.internal/caveats/health
http://probate-orchestrator-service-pr-334.service.core-compute-preview.internal/health
http://probate-submit-service-pr-334.service.core-compute-preview.internal/health
http://probate-submit-service-pr-334.service.core-compute-preview.internal/health
http://probate-business-service-pr-334.service.core-compute-preview.internal/health
http://probate-back-office-pr-1101.service.core-compute-preview.internal/health
```

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


    
