package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.partitioningBy;

@Component
public class BOCaseTransformer {

    public CallbackRequest transformExecutors(@RequestBody CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (caseData.getOtherExecutorExists().equalsIgnoreCase("No")) {
            return callbackRequest;
        }

        Map<Boolean, List<AdditionalExecutors>> executorsMap =
                caseData.getSolsAdditionalExecutorList().stream()
                        .collect(partitioningBy(executor -> executor.getAdditionalExecutor()
                                .getAdditionalApplying().equalsIgnoreCase("Yes")));

        callbackRequest.getCaseDetails().getData().setExecutorsNotApplying(executorsMap.get(false));
        callbackRequest.getCaseDetails().getData().setExecutorsApplying(executorsMap.get(true));
        caseData.getSolsAdditionalExecutorList().clear();

        return callbackRequest;
    }

}