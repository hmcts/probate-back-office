package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.IhtEstate400421BusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

@RequiredArgsConstructor
@Service
public class Iht400421Defaulter {

    private final IhtEstate400421BusinessRule ihtEstate400421BusinessRule;

    public void defaultPageFlowForIht400421(CaseData data,
                                            ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        boolean showIht400421Page = false;
        String ihtFormId = data.getIhtFormId();
        if (StringUtils.isEmpty(ihtFormId) && ihtEstate400421BusinessRule.isApplicable(data)) {
            showIht400421Page = true;
        } else if (IHT400421_VALUE.equals(ihtFormId)) {
            showIht400421Page = true;
        }
        responseCaseDataBuilder.showIht400421Page(showIht400421Page ? YES : NO);
    }
}
