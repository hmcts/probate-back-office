package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Service
public class SolicitorPaymentReferenceDefaulter {

    public void defaultSolicitorReference(CaseData data,
                                          ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {
        responseCaseDataBuilder.solsPBAPaymentReference(data.getSolsSolicitorAppReference());
    }

    public void defaultCaveatSolicitorReference(CaveatData data,
                                                ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder) {
        responseCaseDataBuilder.solsPBAPaymentReference(data.getSolsSolicitorAppReference());
    }
}
