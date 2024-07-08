package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private final CoreCaseDataService coreCaseDataService;
    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public void makeCaseReferenceDormant(List<String> caseReferenceList) {
        log.info("Make Data Migration issue cases to Dormant for cases : {}", caseReferenceList);
        for (String caseReference : caseReferenceList) {
            Optional<CaseDetails> caseDetails = coreCaseDataService.findCaseById(caseReference,
                    securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO());
            if (caseDetails.isPresent()) {
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
                log.error("Case not found for case reference : {}", caseReference);
            }
        }
    }
}
