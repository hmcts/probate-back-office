package uk.gov.hmcts.probate.service.migration;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.DataMigrationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;

import java.util.List;



@Slf4j
@Component
public class Dtspb5113GorMigration implements GorMigrationHandler {
    private static final List<String> POST_GRANT_STATE_LIST = List.of(
            "BOPostGrantIssued",
            "BOExaminingReissue",
            "BOCaseMatchingReissue",
            "BOCaseStoppedReissue",
            "BOGrantIssuedRegistrarEscalation",
            "BOPostGrantIssuedRegistrarEscalation");

    private final AuditEventService auditEventService;
    private final SecurityUtils securityUtils;

    public Dtspb5113GorMigration(AuditEventService auditEventService,
                                 SecurityUtils securityUtils) {
        this.auditEventService = auditEventService;
        this.securityUtils = securityUtils;
    }

    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest, JSONObject migrationData) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        auditEventService.getLatestAuditEventExcludingDormantState(
                String.valueOf(caseDetails.getId()),
                POST_GRANT_STATE_LIST,
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation())
            .ifPresentOrElse(auditEvent -> {
                log.info("Audit event found: Case ID = {}, Event State = {}",
                        caseDetails.getId(), auditEvent.getStateId());
                caseDetails.setState(auditEvent.getStateId());
            }, () -> {
                    log.info("Audit event NOT found: Case ID = {}", caseDetails.getId());
                    throw new DataMigrationException("No audit event found for case ID: " + caseDetails.getId());
            });



        return callbackRequest;
    }
}
