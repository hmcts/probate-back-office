package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseStoppedService {

    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;


    public void caseStopped(CaseDetails caseDetails) {
        log.info("Case stopped: {} ", caseDetails.getId());

        securityUtils.setSecurityContextUserAsCaseworker();

        GrantOfRepresentationData grantOfRepresentationData =
                GrantOfRepresentationData.builder().grantStoppedDate(LocalDate.now()).build();

        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseDetails.getId().toString(),
                grantOfRepresentationData, EventId.GRANT_STOPPED_DATE, securityUtils.getSecurityDTO());

    }

    public void caseResolved(CaseDetails caseDetails) {
        log.info("Case resolved: {} ", caseDetails.getId());

        securityUtils.setSecurityContextUserAsCaseworker();

        CaseData caseData = caseDetails.getData();

        if ((StringUtils.isEmpty(caseData.getGrantDelayedNotificationSent())
                || caseData.getGrantDelayedNotificationSent().equals(Constants.NO))
                && caseData.getGrantStoppedDate() != null
                && caseData.getGrantDelayedNotificationDate() != null) {

            LocalDate now = LocalDate.now();
            Period period = Period.between(caseData.getGrantStoppedDate(), now);

            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                    .grantDelayedNotificationDate(caseData.getGrantDelayedNotificationDate()
                            .plusDays(period.getDays())).build();

            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseDetails.getId().toString(),
                    grantOfRepresentationData, EventId.GRANT_RESOLVED, securityUtils.getSecurityDTO());
        }
    }
}
