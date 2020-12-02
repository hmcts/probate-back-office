package uk.gov.hmcts.probate.service.consumer.util;


import static uk.gov.hmcts.reform.probate.pact.dsl.ObjectMapperTestUtil.convertObjectToJsonString;

import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import org.springframework.beans.factory.annotation.Value;

public  class PactDslFixtureHelper {

  @Value("${ccd.jurisdictionid}")
  String jurisdictionId;

  @Value("${ccd.casetype}")
  String caseType;

  @Value("${ccd.eventid.create}")
  static String createEventId;

  public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
  public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";


  private static final String VALID_PAYLOAD_PATH = "json/probate-casedata-map.json";

  public static CaseDataContent getCaseDataContent() throws Exception {

    final String caseData = ResourceLoader.loadJson(VALID_PAYLOAD_PATH);

    final StartEventResponse startEventResponse = StartEventResponse.builder()
      .eventId(createEventId)
      .token(SOME_AUTHORIZATION_TOKEN)
      .build();

    final CaseDataContent caseDataContent = CaseDataContent.builder()
      .eventToken(startEventResponse.getToken())
      .event(
        Event.builder()
          .id(startEventResponse.getEventId())
          .summary("probateSummary")
          .description("probate")
          .build()
      ).data(convertObjectToJsonString(caseData))
      .build();

    return caseDataContent;
  }
}

