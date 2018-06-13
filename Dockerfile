FROM gradle:jdk8 as builder

COPY . /home/gradle/src
USER root
RUN chown -R gradle:gradle /home/gradle/src
USER gradle

WORKDIR /home/gradle/src
RUN gradle test

FROM openjdk:8-alpine

RUN mkdir -p /opt/app/

WORKDIR /opt/app

COPY docker/entrypoint.sh /
COPY --from=builder /home/gradle/src/build/libs/sol-ccd-service*.jar /opt/app/sol-ccd-service.jar

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy= curl --silent --fail http://localhost:4104/health

EXPOSE 4104

ENTRYPOINT [ "/entrypoint.sh" ]
