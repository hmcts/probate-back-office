package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class NumberOfApplyingExecutorsValidationRule {

    private static final String TOO_MANY_EXECUTORS = "tooManyExecutors";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final int MAX_EXECUTORS = 4;
    private final ExecutorsTransformer executorsTransformer;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(TOO_MANY_EXECUTORS, args, Locale.UK);

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
            executorsTransformer.createCaseworkerApplyingList(caseDetails.getData());
        execsApplying = executorsTransformer.setExecutorApplyingListWithSolicitorInfo(execsApplying,
            caseDetails.getData());
        String execsApplyingNames = FormattingService.createExecsApplyingNames(execsApplying);

        List<String> executors = Arrays.asList(execsApplyingNames.split(","));

        if (executors.size() > MAX_EXECUTORS) {
            throw new BusinessValidationException(userMessage,
                "The total number executors applying cannot exceed 4 for case id " + caseDetails.getId());
        }
    }
}
