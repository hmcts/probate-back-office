package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.HandOffLegacyService;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandOffLegacyTransformer {

    private final HandOffLegacyService handOffLegacyService;

    public void setHandOffToLegacySiteYes(CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();
        if (handOffLegacyService.setCaseToHandedOffToLegacySite(callbackRequest.getCaseDetails())) {
            caseData.setCaseHandedOffToLegacySite(YES);
        } else {
            caseData.setCaseHandedOffToLegacySite(NO);
        }
    }
}
