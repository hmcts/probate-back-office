package uk.gov.hmcts.probate.transformer.reset;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.TitleAndClearingTypeService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

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
            caseData.setAdditionalExecutorsTrustCorpList(null);
        } else if (titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseData)) {
            caseData.setOtherPartnersApplyingAsExecutors(null);
        } else {
            caseData.setAdditionalExecutorsTrustCorpList(null);
            caseData.setOtherPartnersApplyingAsExecutors(null);
        }
    }

    private void resetPowerReservedExecutorsList(CaseData caseData) {
        if (NO.equals(caseData.getDispenseWithNotice())) {
            caseData.setDispenseWithNoticeOtherExecsList(null);
        }
    }
}
