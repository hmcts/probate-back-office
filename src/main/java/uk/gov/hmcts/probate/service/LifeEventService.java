package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class LifeEventService {

    public static final String LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION = "Life Event Verification successful";
    public static final String LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY = "Review LEV tab, proceed to other checks";
    public static final String LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION = "Found no matching death records";
    public static final String LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY = "Stop case and request system number";
    public static final String LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION = "Multiple death records returned";
    public static final String LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY = "View LEV tab and use next steps to " 
        + "make a selection";
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

    public DeathRecord getDeathRecordById(final Integer systemNumber) {
        log.info("Trying LEV call");
        V1Death record;
        try {
            record = deathService.getDeathRecordById(systemNumber);
        } catch (Exception e) {
            log.error("Error during LEV call", e);
            throw e;
        }

        if (null == record) {
            String message = String.format("No death record found with system number %s", systemNumber);
            throw new BusinessValidationException(message, message);
        }

        return deathRecordService.mapDeathRecordCCD(record);
    }

    @Async
    public void verifyDeathRecord(final CaseDetails caseDetails, final SecurityDTO securityDTO) {
        final CaseData caseData = caseDetails.getData();
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final LocalDate deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();
        final String caseId = caseDetails.getId().toString();
        log.info("Trying LEV call");
        List<V1Death> records = emptyList();
        try {
            records = deathService
                .searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname, deceasedDateOfDeath);
        } catch (Exception e) {
            log.error("Error during LEV call", e);
            throw e;

        }
        log.info("LEV Records returned: " + records.size());
        if (1 == records.size()) {
            updateCCDLifeEventVerified(caseId, records, securityDTO);
        } else if (records.isEmpty()) {
            updateCCDLifeEventVerificationNoRecordsFound(caseId, securityDTO);
        } else {
            updateCCDLifeEventVerificationMultipleRecordsFound(caseId, records, securityDTO);
        }
    }

    private void updateCCDLifeEventVerified(final String caseId,
                                            final List<V1Death> records,
                                            final SecurityDTO securityDTO) {

        log.info("LEV updateCCDLifeEventVerified: " + caseId);

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
            LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY
        );
    }

    private void updateCCDLifeEventVerificationNoRecordsFound(final String caseId, final SecurityDTO securityDTO) {

        log.info("LEV updateCCDLifeEventVerificationNoRecordsFound: " + caseId);

        ccdClientApi.updateCaseAsCitizen(
            CcdCaseType.GRANT_OF_REPRESENTATION,
            caseId,
            null,
            EventId.DEATH_RECORD_VERIFICATION_FAILED,
            securityDTO,
            LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY
        );
    }

    private void updateCCDLifeEventVerificationMultipleRecordsFound(final String caseId,
                                                                    final List<V1Death> records,
                                                                    final SecurityDTO securityDTO) {

        log.info("LEV updateCCDLifeEventVerificationMultipleRecordsFound: " + caseId);

        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
            .builder()
            .deathRecords(deathRecordService.mapDeathRecords(records))
            .build();

        ccdClientApi.updateCaseAsCitizen(
            CcdCaseType.GRANT_OF_REPRESENTATION,
            caseId,
            grantOfRepresentationData,
            EventId.DEATH_RECORD_VERIFICATION_FAILED,
            securityDTO,
            LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY
        );
    }
}
