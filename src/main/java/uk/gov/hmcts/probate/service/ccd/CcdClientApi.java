package uk.gov.hmcts.probate.service.ccd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.ConcurrentDataUpdateException;
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
import uk.gov.hmcts.reform.probate.model.cases.CaseData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.reform.probate.model.cases.JurisdictionId.PROBATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CcdClientApi implements CoreCaseDataService {

    public static final String PROBATE_APPLICATION = "Probate application";
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
        CaseDataContent caseDataContent = createCaseDataContent(object, eventId, startEventResponse,
                PROBATE_APPLICATION, PROBATE_APPLICATION);
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
                Map.of("case.legacyId", legacyId.toString()));
        if (caseDetails == null || caseDetails.isEmpty()) {
            return Optional.empty();
        }
        if (caseDetails.size() > 1) {
            throw new IllegalStateException("Multiple cases exist with legacyId provided!");
        }
        return Optional.of(caseDetails.get(0));
    }

    @Override
    public CaseDetails updateCaseAsCaseworker(CcdCaseType caseType, String caseId, LocalDateTime lastModified,
                                              Object caseData, EventId eventId,
                                              SecurityDTO securityDTO, String description, String summary) {
        log.info("Update case as for caseType: {}, caseId: {}, eventId: {}",
                caseType.getName(), caseId, eventId.getName());
        log.info("Retrieve event token from CCD for Caseworker, caseType: {}, caseId: {}, eventId: {}",
                caseType.getName(), caseId, eventId.getName());
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                caseType.getName(),
                caseId,
                eventId.getName()
        );
        //check case not updated in-between DTSPB-3367
        if (startEventResponse.getCaseDetails().getLastModified().truncatedTo(ChronoUnit.MILLIS)
            .isAfter(lastModified)) {
            throw new ConcurrentDataUpdateException(
                String.format("caseId: %s not updated as working with out of date case details", caseId));
        }
        CaseDataContent caseDataContent = createCaseDataContent(caseData, eventId, startEventResponse,
                description, summary);
        log.info("Submit event to CCD for Caseworker, caseType: {}, caseId: {}",
                caseType.getName(), caseId);
        return coreCaseDataApi.submitEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                caseType.getName(),
                caseId,
                false,
                caseDataContent
        );
    }

    @Override
    public CaseDetails updateCaseAsCitizen(CcdCaseType caseType, String caseId, CaseData caseData, EventId eventId,
                                              SecurityDTO securityDTO, String description, String summary) {
        log.info("Retrieve event token from CCD for citizen, caseType: {}, caseId: {}, eventId: {}",
                caseType.getName(), caseId, eventId.getName());
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCitizen(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                caseType.getName(),
                caseId,
                eventId.getName()
        );
        CaseDataContent caseDataContent = createCaseDataContent(caseData, eventId, startEventResponse, description,
                summary);
        log.info("Submit event to CCD for citizen, caseType: {}, caseId: {}",
                caseType.getName(), caseId);
        return coreCaseDataApi.submitEventForCitizen(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                caseType.getName(),
                caseId,
                false,
                caseDataContent
        );
    }

    @Override
    public CaseDetails readForCaseWorker(CcdCaseType caseType, String caseId, SecurityDTO securityDTO) {
        return coreCaseDataApi.readForCaseWorker(
            securityDTO.getAuthorisation(),
            securityDTO.getServiceAuthorisation(),
            securityDTO.getUserId(),
            PROBATE.name(),
            caseType.getName(),
            caseId
        );
    }

    private CaseDataContent createCaseDataContent(Object object,
                                                  EventId eventId, StartEventResponse startEventResponse,
                                                  String description, String summary) {
        return CaseDataContent.builder()
                .event(createEvent(eventId, description, summary))
                .eventToken(startEventResponse.getToken())
                .data(object)
                .build();
    }

    private Event createEvent(EventId eventId, String description, String summary) {
        return Event.builder()
                .id(eventId.getName())
                .description(description)
                .summary(summary)
                .build();
    }
}
