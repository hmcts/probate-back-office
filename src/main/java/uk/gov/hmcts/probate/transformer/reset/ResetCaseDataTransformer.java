package uk.gov.hmcts.probate.transformer.reset;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.TitleAndClearingTypeService;

import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@Slf4j
@AllArgsConstructor
public class ResetCaseDataTransformer {

    private final TitleAndClearingTypeService titleAndClearingTypeService;

    public void resetExecutorLists(CaseData caseData) {
        resetTitleAndClearingExecutorLists(caseData);
        resetPowerReservedExecutorsList(caseData);
    }

    private void resetTitleAndClearingExecutorLists(CaseData caseData) {
        if (titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseData)) {
            Optional.ofNullable(caseData.getAdditionalExecutorsTrustCorpList()).ifPresent(List::clear);
        } else if (titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseData)) {
            Optional.ofNullable(caseData.getOtherPartnersApplyingAsExecutors()).ifPresent(List::clear);
        } else {
            Optional.ofNullable(caseData.getAdditionalExecutorsTrustCorpList()).ifPresent(List::clear);
            Optional.ofNullable(caseData.getOtherPartnersApplyingAsExecutors()).ifPresent(List::clear);
        }
    }

    private void resetPowerReservedExecutorsList(CaseData caseData) {
        if (NO.equals(caseData.getDispenseWithNotice())) {
            Optional.ofNullable(caseData.getDispenseWithNoticeOtherExecsList()).ifPresent(List::clear);
        }
    }
}
