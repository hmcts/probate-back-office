package uk.gov.hmcts.probate.transformer.reset;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.TitleAndClearingTypeService;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@Slf4j
@AllArgsConstructor
public class ResetResponseCaseDataTransformer {

    private final TitleAndClearingTypeService titleAndClearingTypeService;

    public void resetTitleAndClearingFields(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        if (titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseData)) {
            nullTrustCorpOptions(builder);
            builder.titleAndClearingTypeNoT(null);

            if (!titleAndClearingTypeService.successorFirmTitleAndClearingOptionSelected(caseData)) {
                builder.nameOfSucceededFirm(null);
            }

        } else if (titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseData)) {
            nullPartnerOptions(builder);
            builder.titleAndClearingTypeNoT(null);

        } else {
            nullTrustCorpOptions(builder);
            nullPartnerOptions(builder);
        }

        if (NO.equals(caseData.getDispenseWithNotice())) {
            builder.dispenseWithNoticeOtherExecsList(null);
        }
    }

    private void nullTrustCorpOptions(ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .additionalExecutorsTrustCorpList(null)
                .trustCorpName(null)
                .trustCorpAddress(null)
                .lodgementAddress(null)
                .lodgementDate(null);
    }

    private void nullPartnerOptions(ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .otherPartnersApplyingAsExecutors(null)
                .nameOfSucceededFirm(null)
                .nameOfFirmNamedInWill(null)
                .whoSharesInCompanyProfits(null)
                .soleTraderOrLimitedCompany(null);
    }

}

