package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_LAY;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_PROFESSIONAL;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
public class ExecutorTypeService {

    public boolean isSolicitorExecutorNamedOnWill(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    public boolean isPartnerExecutor(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();

        return List.of(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING
        ).contains(titleAndClearing);
    }

    public String executorTitleAndClearingType(CaseData caseData) {
        return isPartnerExecutor(caseData) ? EXECUTOR_TYPE_PROFESSIONAL : EXECUTOR_TYPE_TRUST_CORP;
    }

    public String solicitorExecutorType(CaseData caseData) {
        if (isSolicitorExecutorNamedOnWill(caseData)) {
            return EXECUTOR_TYPE_LAY;
        } else {
            return executorTitleAndClearingType(caseData);
        }
    }

}
