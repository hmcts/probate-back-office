package uk.gov.hmcts.probate.service.lifeevents;

import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.HandOffLegacyService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class LifeEventCCDService {

    public static final String LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION = "Life Event Verification successful";
    public static final String LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY = "Review LEV tab, proceed to other checks";
    public static final String LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION = "Found no matching death records";
    public static final String LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY = "Stop case and request system number";
    public static final String LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION = "Multiple death records returned";
    public static final String LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY = "View LEV tab and use next steps to "
        + "make a selection";
    public static final String LIFE_EVENT_VERIFICATION_ERROR_DESCRIPTION = "LEV API failed";
    public static final String LIFE_EVENT_VERIFICATION_ERROR_SUMMARY = "Use the dropdown to manually verify life event";
    private DeathService deathService;
    private CcdClientApi ccdClientApi;
    private DeathRecordService deathRecordService;
    private HandOffLegacyService handOffLegacyService;

    @Autowired
    public LifeEventCCDService(final DeathService deathService, final CcdClientApi ccdClientApi,
                               final DeathRecordService deathRecordService,
                               final HandOffLegacyService handOffLegacyService) {
        this.deathService = deathService;
        this.ccdClientApi = ccdClientApi;
        this.deathRecordService = deathRecordService;
        this.handOffLegacyService = handOffLegacyService;
    }

    @Async
    public void verifyDeathRecord(final CaseDetails caseDetails, final SecurityDTO securityDTO, boolean isCitizenUser) {
        final CaseData caseData = caseDetails.getData();
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final LocalDate deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();
        final String caseId = caseDetails.getId().toString();
        log.info("Trying LEV call " + caseId);
        List<V1Death> records;
        try {
            records = deathService
                .searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname, deceasedDateOfDeath);
        } catch (Exception e) {
            log.error("Error during LEV call", e);
            updateCCDLifeEventVerificationError(caseDetails, caseId, securityDTO, isCitizenUser);
            throw e;
        }
        log.info("LEV Records returned: " + records.size());
        if (1 == records.size()) {
            updateCCDLifeEventVerified(caseDetails, caseId, records, securityDTO, isCitizenUser);
        } else if (records.isEmpty()) {
            updateCCDLifeEventVerificationNoRecordsFound(caseDetails, caseId, securityDTO, isCitizenUser);
        } else {
            updateCCDLifeEventVerificationMultipleRecordsFound(caseDetails, caseId, records, securityDTO,
                    isCitizenUser);
        }
    }

    private void updateCCDLifeEventVerified(final CaseDetails caseDetails,
                                            final String caseId,
                                            final List<V1Death> records,
                                            final SecurityDTO securityDTO,
                                            final boolean isCitizenUser) {

        log.info("LEV updateCCDLifeEventVerified: " + caseId);

        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
            .builder()
            .deathRecords(deathRecordService.mapDeathRecords(records))
            .caseHandedOffToLegacySite(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails))
            .boHandoffReasonList(handOffLegacyService.setHandoffReason(caseDetails))
            .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
            .build();

        updateCaseAsCitizenOrCaseworker(caseDetails, grantOfRepresentationData,
            EventId.DEATH_RECORD_VERIFIED, securityDTO, LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY, isCitizenUser);
    }

    private void updateCCDLifeEventVerificationNoRecordsFound(final CaseDetails caseDetails,
                                                              final String caseId, final SecurityDTO securityDTO,
                                                              final boolean isCitizenUser) {

        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
            .builder()
            .caseHandedOffToLegacySite(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails))
            .boHandoffReasonList(handOffLegacyService.setHandoffReason(caseDetails))
            .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
            .build();

        log.info("LEV updateCCDLifeEventVerificationNoRecordsFound: " + caseId);

        updateCaseAsCitizenOrCaseworker(caseDetails, grantOfRepresentationData,
            EventId.DEATH_RECORD_VERIFICATION_FAILED, securityDTO, LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY, isCitizenUser);
    }

    private void updateCCDLifeEventVerificationMultipleRecordsFound(final CaseDetails caseDetails,
                                                                    final String caseId,
                                                                    final List<V1Death> records,
                                                                    final SecurityDTO securityDTO,
                                                                    final boolean isCitizenUser) {

        log.info("LEV updateCCDLifeEventVerificationMultipleRecordsFound: " + caseId);

        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
            .builder()
            .deathRecords(deathRecordService.mapDeathRecords(records))
            .caseHandedOffToLegacySite(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails))
            .boHandoffReasonList(handOffLegacyService.setHandoffReason(caseDetails))
            .build();

        updateCaseAsCitizenOrCaseworker(caseDetails, grantOfRepresentationData,
            EventId.DEATH_RECORD_VERIFICATION_FAILED, securityDTO, LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY, isCitizenUser);
    }

    private void updateCCDLifeEventVerificationError(final CaseDetails caseDetails,
                                                     final String caseId,
                                                     final SecurityDTO securityDTO, final boolean isCitizenUser) {

        log.info("LEV updateCCDLifeEventVerificationError: " + caseId);

        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
            .builder()
            .caseHandedOffToLegacySite(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails))
            .boHandoffReasonList(handOffLegacyService.setHandoffReason(caseDetails))
            .build();

        updateCaseAsCitizenOrCaseworker(caseDetails, grantOfRepresentationData,
            EventId.DEATH_RECORD_VERIFICATION_FAILED, securityDTO, LIFE_EVENT_VERIFICATION_ERROR_DESCRIPTION,
            LIFE_EVENT_VERIFICATION_ERROR_SUMMARY, isCitizenUser);
    }

    private void updateCaseAsCitizenOrCaseworker(CaseDetails caseDetails,
                                                 GrantOfRepresentationData grantOfRepresentationData, EventId eventId,
                                                 SecurityDTO securityDTO, String description, String summary,
                                                 boolean isCitizenUser) {
        if (isCitizenUser) {
            ccdClientApi.updateCaseAsCitizen(CcdCaseType.GRANT_OF_REPRESENTATION, caseDetails.getId().toString(),
                    grantOfRepresentationData, eventId, securityDTO, description, summary);
        } else {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseDetails.getId().toString(),
                    getLastModifiedDate(caseDetails), grantOfRepresentationData, eventId, securityDTO, description,
                    summary);
        }
    }

    LocalDateTime getLastModifiedDate(CaseDetails caseDetails) {
        String[] lastModified = caseDetails.getLastModified();
        if (lastModified != null && lastModified.length >= 7 && lastModified[0] != null && lastModified[1] != null
                && lastModified[2] != null && lastModified[3] != null && lastModified[4] != null
                && lastModified[5] != null && lastModified[6] != null) {
            try {
                return LocalDateTime.of(parseInt(lastModified[0]), parseInt(lastModified[1]), parseInt(lastModified[2]),
                        parseInt(lastModified[3]), parseInt(lastModified[4]), parseInt(lastModified[5]),
                        parseInt(lastModified[6]));
            } catch (NumberFormatException | DateTimeException e) {
                log.warn("LifeEventCCDService.getLastModifiedDate for case {}, error {}", caseDetails.getId(),
                        e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}
