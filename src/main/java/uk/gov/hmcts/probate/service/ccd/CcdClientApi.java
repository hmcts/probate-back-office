package uk.gov.hmcts.probate.service.ccd;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.JurisdictionId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.CoreCaseDataService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

@Component
@RequiredArgsConstructor
public class CcdClientApi implements CoreCaseDataService {

    private final CoreCaseDataApi coreCaseDataApi;

    @Override
    public CaseDetails createCase(Object object, CcdCaseType ccdCaseType, EventId eventId, SecurityDTO securityDTO) {
        StartEventResponse startEventResponse = coreCaseDataApi.startForCaseworker(
            securityDTO.getAuthorisation(),
            securityDTO.getServiceAuthorisation(),
            securityDTO.getUserId(),
            JurisdictionId.PROBATE.name(),
            ccdCaseType.getName(),
            eventId.getName()
        );
        CaseDataContent caseDataContent = createCaseDataContent(object, eventId, startEventResponse);
        CaseDetails caseDetails = coreCaseDataApi.submitForCaseworker(
            securityDTO.getAuthorisation(),
            securityDTO.getServiceAuthorisation(),
            securityDTO.getUserId(),
            JurisdictionId.PROBATE.name(),
            ccdCaseType.getName(),
            false,
            caseDataContent
        );
        return caseDetails;
    }

    private CaseDataContent createCaseDataContent(Object object, EventId eventId, StartEventResponse startEventResponse) {
        return CaseDataContent.builder()
            .event(createEvent(eventId))
            .eventToken(startEventResponse.getToken())
            .data(object)
            .build();
    }

    private Event createEvent(EventId eventId) {
        return Event.builder()
            .id(eventId.getName())
            .description("Probate application")
            .summary("Probate application")
            .build();
    }
}
