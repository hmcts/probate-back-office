package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdditionalExecutorsListFilter {

    private static final String NO = "No";

    public List<AdditionalExecutors> filter(List<AdditionalExecutors> additionalExecutors, CaseData caseData) {

        if (caseData.getOtherExecutorExists().equalsIgnoreCase(NO)){
            return Collections.emptyList();
        }

        return additionalExecutors.stream().filter(additionalExecutor ->
                    additionalExecutor.getAdditionalExecutor().getAdditionalApplying().equalsIgnoreCase(caseData.getPrimaryApplicantIsApplying()))
                    .collect(Collectors.toList());
    }

}

