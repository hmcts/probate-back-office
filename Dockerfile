FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0
# Mandatory!
ENV APP back-office.jar

COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:4104/health || exit 1

EXPOSE 4104

CMD [ "back-office.jar" ]
