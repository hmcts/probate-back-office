 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.5.4
FROM hmctspublic.azurecr.io/base/java:17-distroless
LABEL maintainer="https://github.com/hmcts/probate-back-office"

COPY build/libs/back-office.jar /opt/app/
COPY lib/applicationinsights.json /opt/app/

EXPOSE 4104

CMD [ "back-office.jar" ]
