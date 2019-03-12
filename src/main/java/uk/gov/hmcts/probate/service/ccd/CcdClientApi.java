package uk.gov.hmcts.probate.service.ccd;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CcdClientApi implements CoreCaseDataService {

    private final CoreCaseDataApi coreCaseDataApi;

    @Override
    public CaseDetails createCase(Object object, CcdCaseType ccdCaseType, EventId eventId, SecurityDTO securityDTO) {
        log.info("Start event for create case");
        StartEventResponse startEventResponse = coreCaseDataApi.startForCaseworker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                ccdCaseType.getName(),
                eventId.getName()
        );
        CaseDataContent caseDataContent = createCaseDataContent(object, eventId, startEventResponse);
        log.info("Submit case for create case");
        return coreCaseDataApi.submitForCaseworker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                ccdCaseType.getName(),
                false,
                caseDataContent
        );
    }

    @Override
    public Optional<CaseDetails> retrieveCaseByLegacyId(String caseType, Long legacyId, SecurityDTO securityDTO) {
        log.info("Search for imported legacy case in CCD by legacyId: {}", legacyId);
        List<CaseDetails> caseDetails = coreCaseDataApi.searchForCaseworker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType,
                ImmutableMap.of("case.legacyId", legacyId.toString()));
        if (caseDetails == null || caseDetails.isEmpty()) {
            return Optional.empty();
        }
        if (caseDetails.size() > 1) {
            throw new IllegalStateException("Multiple cases exist with legacyId provided!");
        }
        return Optional.of(caseDetails.get(0));
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
