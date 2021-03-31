package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FormattingService {

    private FormattingService() {
    }

    // Create a formatted string including all applying execs
    public static String createExecsApplyingNames(List<CollectionMember<AdditionalExecutorApplying>> execs) {
        if (execs.isEmpty()) {
            return "None";
        }

        StringBuilder names = new StringBuilder();
        String finalName = execs.get(execs.size() - 1).getValue().getApplyingExecutorName();
        execs.remove(execs.size() - 1);
        execs.forEach(exec -> names.append(exec.getValue().getApplyingExecutorName()).append(", "));
        names.append(finalName);

        return capitaliseEachWord(names.toString());
    }

    // Create a formatted string including all not applying execs
    public static String createExecsNotApplyingNames(List<CollectionMember<AdditionalExecutorNotApplying>> execs) {
        if (execs.isEmpty()) {
            return "None";
        }

        StringBuilder names = new StringBuilder();
        String finalName = execs.get(execs.size() - 1).getValue().getNotApplyingExecutorName();
        execs.remove(execs.size() - 1);
        execs.forEach(exec -> names.append(exec.getValue().getNotApplyingExecutorName()).append(", "));
        names.append(finalName);

        return capitaliseEachWord(names.toString());
    }

    public static String capitaliseEachWord(String name) {
        return Arrays.stream(name.split("\\s+"))
                        .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                        .collect(Collectors.joining(" "));
    }

    public static String getSolsSOTName(String firstNames, String surname) {
        return firstNames + " " + surname;
    }

}
