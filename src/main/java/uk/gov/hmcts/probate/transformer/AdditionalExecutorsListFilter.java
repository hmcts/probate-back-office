package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdditionalExecutorsListFilter {

    public List<AdditionalExecutors> filter(List<AdditionalExecutors> additionalExecutors, String isApplying, String otherExists) {
        if (otherExists.equalsIgnoreCase("No")) {
            return null;
        }
        return additionalExecutors.stream().filter(additionalExecutor ->
                    additionalExecutor.getAdditionalExecutor().getAdditionalApplying().equalsIgnoreCase(isApplying))
                    .collect(Collectors.toList());
    }

}

