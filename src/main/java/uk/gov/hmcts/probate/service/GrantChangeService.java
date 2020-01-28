package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.ADMON_WILL_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@Slf4j
@Component
public class GrantChangeService {
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";

    public ResponseCaseDataBuilder clearGrantSpecificData(CallbackRequest callbackRequest, ResponseCaseDataBuilder responseCaseDataBuilder, String newState) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        switch (caseData.getCaseType()) {
            case GRANT_OF_PROBATE_NAME:
                if (!STATE_GRANT_TYPE_PROBATE.equals(newState)) {
                    responseCaseDataBuilder
                            .willAccessOriginal(null)
                            .willHasCodicils(null)
                            .willNumberOfCodicils(null)
                            .primaryApplicantHasAlias(null)
                            .solsExecutorAliasNames(null)
                            .solsPrimaryExecutorNotApplyingReason(null)
                            .otherExecutorExists(null)
                            .solsAdditionalExecutorList(null);
                }
                break;
            case INTESTACY_NAME:
                if (!STATE_GRANT_TYPE_INTESTACY.equals(newState)) {
                    responseCaseDataBuilder
                            .solsMinorityInterest(null)
                            .solsApplicantSiblings(null)
                            .primaryApplicantPhoneNumber(null)
                            .primaryApplicantEmailAddress(null)
                            .deceasedMaritalStatus(null)
                            .solsApplicantRelationshipToDeceased(null)
                            .solsSpouseOrCivilRenouncing(null)
                            .solsAdoptedEnglandOrWales(null);
                }
                break;
            case ADMON_WILL_NAME:
                if (!STATE_GRANT_TYPE_ADMON.equals(newState)) {
                    responseCaseDataBuilder
                            .willAccessOriginal(null)
                            .willHasCodicils(null)
                            .willNumberOfCodicils(null)
                            .solsEntitledMinority(null)
                            .solsDiedOrNotApplying(null)
                            .solsResiduary(null)
                            .solsResiduaryType(null)
                            .solsLifeInterest(null)
                            .primaryApplicantPhoneNumber(null)
                            .primaryApplicantEmailAddress(null);
                }
                break;
        }

        return responseCaseDataBuilder;
    }
}
