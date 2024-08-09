package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.AuditEvent;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationIssueDormantCaseService {

    public static final String DORMANT_SUMMARY = "This case has been moved to "
            + "the dormant state due to no action or event on the case for 6 months";
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    private final AuditEventService auditEventService;
    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;
    private final List<String> excludedEventList = Arrays.asList("boHistoryCorrection", "boCorrection");

    public void makeCaseReferenceDormant(List<String> caseReferenceList, LocalDateTime dormancyPeriod) {
        log.info("Make Data Migration issue cases to Dormant for cases : {}", caseReferenceList);
        for (String caseReference : caseReferenceList) {
            Optional<CaseDetails> caseDetails = ccdClientApi.findCaseById(caseReference,
                    securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO());
            if (caseDetails.isPresent()) {
                AuditEvent auditEvent = getAuditEvent(caseDetails.get().getId(),
                        securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO());
                log.info("Event created {}", auditEvent);
                log.info("Dormancy period date {}", dormancyPeriod);
                if (auditEvent.getCreatedDate().isBefore(dormancyPeriod)
                        || auditEvent.getCreatedDate().isEqual(dormancyPeriod)) {
                    GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                            .moveToDormantDateTime(LocalDateTime.now(ZoneOffset.UTC)
                                    .plusMinutes(makeDormantAddTimeMinutes))
                            .build();
                    log.info("Updating Data Migration issue case to Dormant in CCD by scheduler for case id : {}",
                            caseReference);
                    try {
                        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                                caseReference,
                                caseDetails.get().getLastModified(),
                                grantOfRepresentationData, EventId.MAKE_CASE_DORMANT,
                                securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), DORMANT_SUMMARY,
                                DORMANT_SUMMARY);
                        log.info("Updated Data Migration issue case to Dormant in CCD by scheduler for case id : {}",
                                caseReference);
                    } catch (Exception e) {
                        log.error("Dormant case error: Case:{}, cannot be moved in Dormant state {}",
                                caseReference,e.getMessage());
                    }
                } else {
                    log.info("Case {} is not eligible for Dormant", caseReference);
                }
            } else {
                log.error("Case not found for case reference : {}", caseReference);
            }
        }
    }

    private AuditEvent getAuditEvent(Long caseId, SecurityDTO securityDTO) {
        return auditEventService.getLatestAuditEventByName(caseId.toString(), excludedEventList,
                securityDTO.getAuthorisation(), securityDTO.getServiceAuthorisation())
                .orElseThrow(() -> new IllegalStateException(String
                .format("Could not find any event other than %s event in audit", excludedEventList)));
    }
}
