package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.service.HandOffLegacyService;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.List;

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
            List<CollectionMember<HandoffReason>> handoffReasonsList =
                    handOffLegacyService.setHandoffReason(callbackRequest.getCaseDetails())
                        .stream()
                        .map(uk.gov.hmcts.reform.probate.model.cases.CollectionMember::getValue)
                        .map(this::buildHandOffReason)
                        .toList();
            caseData.setBoHandoffReasonList(handoffReasonsList);
        } else {
            caseData.setCaseHandedOffToLegacySite(NO);
        }
    }

    private CollectionMember<HandoffReason> buildHandOffReason(HandoffReason reason) {
        return new CollectionMember<>(null, reason);
    }

    public void resetHandOffToLegacySite(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        caseData.setCaseHandedOffToLegacySite(null);
        caseData.setBoHandoffReasonList(null);
    }
}
