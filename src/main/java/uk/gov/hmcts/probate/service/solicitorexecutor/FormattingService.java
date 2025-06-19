package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
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

        return capitaliseEachWord(names.toString(), "Executors applying names");
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

        return capitaliseEachWord(names.toString(),  "Executors not applying names");
    }

    public static String capitaliseEachWord(
            final String input,
            final String description) {
        if (input == null) {
            return null;
        }

        final String[] parts = input.split("\\s+");
        if (Arrays.stream(parts).anyMatch(String::isEmpty)) {
            final StringBuilder errBuilder = new StringBuilder();
            errBuilder.append("One of the provided inputs for capitalisation of [")
                    .append(description)
                    .append("] is an empty string: [\"")
                    .append(Arrays.stream(parts).collect(Collectors.joining("\", \"")))
                    .append("\"]");
            final FormattingServiceException fse = new FormattingServiceException(errBuilder.toString());
            log.info("One of the components of an input is an empty string", fse);
            throw fse;
        }
        return Arrays.stream(input.split("\\s+"))
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
