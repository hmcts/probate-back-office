package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static uk.gov.hmcts.reform.probate.model.InOut.Constants.IN_VALUE;
import static uk.gov.hmcts.reform.probate.model.InOut.Constants.OUT_VALUE;

@RequiredArgsConstructor
public enum InOutNew {

    IN(IN_VALUE), OUT(OUT_VALUE);

    @Getter
    private final String description;

    public static class Constants {

        public static final String IN_VALUE = "in";

        public static final String OUT_VALUE = "out";

        public static final String ALLOWABLE_VALUES = IN_VALUE + "," + OUT_VALUE;

        private Constants() {
        }
    }
}