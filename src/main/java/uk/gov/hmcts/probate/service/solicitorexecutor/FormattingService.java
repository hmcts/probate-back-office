package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
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

        var names = new StringBuilder();
        final String finalName = execs.get(execs.size() - 1).getValue().getApplyingExecutorName();
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

        var names = new StringBuilder();
        final String finalName = execs.get(execs.size() - 1).getValue().getNotApplyingExecutorName();
        execs.remove(execs.size() - 1);
        execs.forEach(exec -> names.append(exec.getValue().getNotApplyingExecutorName()).append(", "));
        names.append(finalName);

        return capitaliseEachWord(names.toString());
    }

    public static String capitaliseEachWord(String name) {
        if (name == null) {
            return null;
        }

        final String[] parts = name.split("\\s+");
        if (Arrays.stream(parts).anyMatch(String::isEmpty)) {
            final StringBuilder errBuilder = new StringBuilder();
            errBuilder.append("One of the provided inputs for capitalisation is an empty string: [\"");
            errBuilder.append(Arrays.stream(parts).collect(Collectors.joining("\", \"")));
            errBuilder.append("\"]");
            throw new FormattingServiceException(errBuilder.toString());
        }
        return Arrays.stream(name.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static String getSolsSOTName(String firstNames, String surname) {
        return firstNames + " " + surname;
    }

    public static class FormattingServiceException extends BusinessValidationException {
        FormattingServiceException(String userMessage) {
            super(userMessage, "exception when capitalising words");
        }
    }
}
