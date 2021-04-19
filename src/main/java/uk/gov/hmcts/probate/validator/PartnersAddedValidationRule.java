package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class PartnersAddedValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String PARTNERS_NEEDED = "partnersNeeded";
    private static final String PARTNERS_NEEDED_TRUST_CORPS = "partnersNeededTrustCorp";

    public void validate(CaseDetails caseDetails) {

        final CaseData caseData = caseDetails.getData();
        final String[] args = {caseDetails.getId().toString()};
        final String titleAndClearing = caseData.getTitleAndClearingType();
        final String userMessage = businessValidationMessageRetriever.getMessage(PARTNERS_NEEDED, args, Locale.UK);
        final String userMessageTrustCorps = businessValidationMessageRetriever.getMessage(PARTNERS_NEEDED_TRUST_CORPS,
                args, Locale.UK);

        final List<String> nonTrustPtnrTitleClearingTypes = new ArrayList<String>(asList(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING));

        final List<String> trustPtnrTitleClearingTypes = new ArrayList<String>(asList(
                TITLE_AND_CLEARING_TRUST_CORP_SDJ,
                TITLE_AND_CLEARING_TRUST_CORP));

        // Must have other partners for trust corp / firm if NOT (not named in will, and applying)

        if (!(NO.equals(caseData.getSolsSolicitorIsExec()) && YES.equals(caseData.getSolsSolicitorIsApplying()))) {
            if (NO.equals(caseData.getAnyOtherApplyingPartners())
                    && nonTrustPtnrTitleClearingTypes.contains(titleAndClearing)) {
                throw new BusinessValidationException(userMessage,
                        "'Yes' needs to be selected for question anyOtherApplyingPartners for case id "
                                + caseDetails.getId());
            }

            if (NO.equals(caseData.getAnyOtherApplyingPartnersTrustCorp())
                    && trustPtnrTitleClearingTypes.contains(titleAndClearing)) {
                throw new BusinessValidationException(userMessageTrustCorps,
                        "'Yes' needs to be selected for question anyOtherApplyingPartnersTrustCorp for case id "
                                + caseDetails.getId());
            }
        }
    }
}
