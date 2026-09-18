package uk.gov.hmcts.probate.dmnutils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
public class DelayUntilRequest {

    private String delayUntil;
    private String delayUntilTime;
    private String delayUntilOrigin;
    private Integer delayUntilIntervalDays;
    private String delayUntilNonWorkingCalendar;
    private String delayUntilNonWorkingDaysOfWeek;
    private Boolean delayUntilSkipNonWorkingDays;
    private String delayUntilMustBeWorkingDay;

    @JsonCreator
    public DelayUntilRequest(
        @JsonProperty("delayUntil")
        String delayUntil,
        @JsonProperty("delayUntilTime")
        String delayUntilTime,
        @JsonProperty("delayUntilOrigin")
        String delayUntilOrigin,
        @JsonProperty("delayUntilIntervalDays")
        Integer delayUntilIntervalDays,
        @JsonProperty("delayUntilNonWorkingCalendar")
        String delayUntilNonWorkingCalendar,
        @JsonProperty("delayUntilNonWorkingDaysOfWeek")
        String delayUntilNonWorkingDaysOfWeek,
        @JsonProperty("delayUntilSkipNonWorkingDays")
        Boolean delayUntilSkipNonWorkingDays,
        @JsonProperty("delayUntilMustBeWorkingDay")
        String delayUntilMustBeWorkingDay) {
        this.delayUntil = delayUntil;
        this.delayUntilTime = delayUntilTime;
        this.delayUntilOrigin = delayUntilOrigin;
        this.delayUntilIntervalDays = delayUntilIntervalDays;
        this.delayUntilNonWorkingCalendar = delayUntilNonWorkingCalendar;
        this.delayUntilNonWorkingDaysOfWeek = delayUntilNonWorkingDaysOfWeek;
        this.delayUntilSkipNonWorkingDays = delayUntilSkipNonWorkingDays;
        this.delayUntilMustBeWorkingDay = delayUntilMustBeWorkingDay;
    }
}
