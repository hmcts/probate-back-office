package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@RequiredArgsConstructor
public class ZeroApplyingExecutorsValidationRule {

    private static final String NO_EXECUTORS = "zeroExecutors";
    private static final String NO_EXECUTORS_WELSH = "zeroExecutorsWelsh";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private final ExecutorsTransformer executorsTransformer;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        CaseData caseData = caseDetails.getData();
        String userMessage = businessValidationMessageRetriever.getMessage(NO_EXECUTORS, args, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever.getMessage(NO_EXECUTORS_WELSH, args, Locale.UK);

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = executorsTransformer.createCaseworkerApplyingList(caseData);
        execsApplying = executorsTransformer.setExecutorApplyingListWithSolicitorInfo(execsApplying, caseDetails.getData());
        String execsApplyingNames = FormattingService.createExecsApplyingNames(execsApplying);

        List<String> executors = Arrays.asList(execsApplyingNames.split(","));

        if (execsApplyingNames.equals("None")
                && executors.size() == 1
                && executors.get(0).equals("None")
                && caseData.getNumberOfExecutors() == 0
                && NO.equals(caseData.getOtherExecutorExists())
                && NO.equals(caseData.getSolsSolicitorIsExec())
                && NO.equals(caseData.getSolsSolicitorIsApplying())) {
            throw new BusinessValidationException(userMessage,
                "There must be at least one executor applying. You have not added " +
                        "an applying probate practitioner or any executors for case id " + caseDetails.getId(), userMessageWelsh);
        }
    }
}
