package uk.gov.hmcts.probate.model;

import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScheduleDates {
    private final Clock clock;
    private final String fromDate;
    private final String toDate;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ScheduleDates(
            final Clock clock,
            final String fromDate,
            final String toDate) {
        this.clock = clock;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public boolean hasValue() {
        return StringUtils.isNotBlank(fromDate);
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return StringUtils.isNotBlank(toDate) ? toDate : fromDate;
    }

    public String getYesterday() {
        final LocalDate today = LocalDate.now(clock);
        final LocalDate yesterday = today.minusDays(1);
        return DATE_FORMAT.format(yesterday);
    }
}