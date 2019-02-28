FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-1.0

# Mandatory!
ENV APP back-office.jar

COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:4104/health || exit 1

EXPOSE 4104

CMD [ "back-office.jar" ]
