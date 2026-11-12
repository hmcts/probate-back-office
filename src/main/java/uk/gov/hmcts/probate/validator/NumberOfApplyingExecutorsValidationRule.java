package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class NumberOfApplyingExecutorsValidationRule {

    private static final String TOO_MANY_EXECUTORS = "tooManyExecutors";
    private static final String TOO_MANY_EXECUTORS_WELSH = "tooManyExecutorsWelsh";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final int MAX_EXECUTORS = 4;
    private final ExecutorsTransformer executorsTransformer;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(TOO_MANY_EXECUTORS, args, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever.getMessage(TOO_MANY_EXECUTORS_WELSH, args,
                Locale.UK);

        CaseData caseData = caseDetails.getData();
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
            executorsTransformer.createCaseworkerApplyingList(caseData);
        execsApplying = executorsTransformer.setExecutorApplyingListWithSolicitorInfo(execsApplying, caseData);

        int applyingExecutorsCount = execsApplying.size();

        // make sure it counts primary applicant as executor and not only the executors applying
        // will only count if both name fields are filled
        final boolean hasPrimaryApplicantName = StringUtils.isNotBlank(caseData.getPrimaryApplicantForenames())
                && StringUtils.isNotBlank(caseData.getPrimaryApplicantSurname());
        // this seems roundabout - we care specifically that the value in the case data for this field is distinct
        // from No (i.e. must be unset OR Yes) so we don't unintentionally pass this check if it's not explicitly set
        final boolean notPrimaryApplicantNotApplying = ! caseData.isPrimaryApplicantNotApplying();

        if (hasPrimaryApplicantName && notPrimaryApplicantNotApplying) {
            applyingExecutorsCount++;
        }

        if (applyingExecutorsCount > MAX_EXECUTORS) {
            throw new BusinessValidationException(userMessage,
                "The total number executors applying cannot exceed 4 for case id " + caseDetails.getId(),
                    userMessageWelsh);
        }
    }
}
