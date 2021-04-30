package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.lifeevents.client.model.V1Death;
import uk.gov.hmcts.lifeevents.client.service.DeathService;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class LifeEventService {

    public static final String LIFE_EVENT_VERIFICATION_SUCCESSFUL = "Life Event Verification successful";
    public static final String REVIEW_LEV_TAB_PROCEED_TO_OTHER_CHECKS = "Review LEV tab, proceed to other checks";
    private DeathService deathService;
    private CcdClientApi ccdClientApi;
    private DeathRecordService deathRecordService;

    @Autowired
    public LifeEventService(final DeathService deathService, final CcdClientApi ccdClientApi, 
                            final DeathRecordService deathRecordService) {
        this.deathService = deathService;
        this.ccdClientApi = ccdClientApi;
        this.deathRecordService = deathRecordService;
    }

    @Async
    public void verifyDeathRecord(final CaseDetails caseDetails, final SecurityDTO securityDTO) {
        final CaseData caseData = caseDetails.getData();
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final LocalDate deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();
        log.info("Trying LEV call");
        List<V1Death> records = deathService
                .searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname, deceasedDateOfDeath);
        log.info("Records returned: " + records.size());
        if (1 == records.size()) {
            updateCCDLifeEventVerified(caseDetails.getId().toString(), records, securityDTO);
        }
    }

    private void updateCCDLifeEventVerified(final String caseId,
                                            final List<V1Death> records,
                                            final SecurityDTO securityDTO) {
        
        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
                .builder()
                .deathRecords(deathRecordService.mapDeathRecords(records))
                .build();

        ccdClientApi.updateCaseAsCitizen(
                CcdCaseType.GRANT_OF_REPRESENTATION,
                caseId,
                grantOfRepresentationData,
                EventId.DEATH_RECORD_VERIFIED,
                securityDTO,
                LIFE_EVENT_VERIFICATION_SUCCESSFUL,
                REVIEW_LEV_TAB_PROCEED_TO_OTHER_CHECKS
        );
    }
}
