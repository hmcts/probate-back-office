package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class IHTFourHundredDateValidationRule implements IHTFourHundredDateRule {

    public static final String IHT_DATE_IS_INVALID = "iht400DateInvalid";
    public static final String IHT_DATE_IS_IN_FUTURE = "iht400DateIsInFuture";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public static long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
                + "," + endDate);
        }
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
            || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long businessDays;
        try {
            businessDays = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween)
                .filter(isWeekend.negate()).count();
        } catch (IllegalArgumentException e) {
            businessDays = -1;
        }
        return businessDays;
    }

    public static LocalDate addBusinessDays(LocalDate localDate, int days) {
        if (localDate == null || days <= 0) {
            throw new IllegalArgumentException("Invalid method argument(s) "
                + "to addBusinessDays(" + localDate + "," + days + ")");
        }
        Predicate<LocalDate> isWeekend = date
            -> date.getDayOfWeek() == DayOfWeek.SATURDAY
            || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        LocalDate result = localDate;
        while (days > 0) {
            result = result.plusDays(1);
            if (isWeekend.negate().test(result)) {
                days--;
            }
        }
        return result;
    }

    public static LocalDate minusBusinessDays(LocalDate localDate, int days) {
        if (localDate == null || days <= 0) {
            throw new IllegalArgumentException("Invalid method argument(s) "
                + "to addBusinessDays(" + localDate + "," + days + ")");
        }
        Predicate<LocalDate> isWeekend = date
            -> date.getDayOfWeek() == DayOfWeek.SATURDAY
            || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        LocalDate result = localDate;
        while (days > 0) {
            result = result.minusDays(1);
            if (isWeekend.negate().test(result)) {
                days--;
            }
        }
        return result;
    }

    @Override
    public void validate(CaseDetails caseDetails) {
        LocalDate iht400Date = caseDetails.getData().getSolsIHT400Date();
        String[] args = {caseDetails.getData().convertDate(addBusinessDays(iht400Date, 20))};
        String userMessage;

        if (countBusinessDaysBetween(iht400Date, LocalDate.now()) < 0) {
            userMessage = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_IN_FUTURE, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ") needs to be in the past");
        }

        if (countBusinessDaysBetween(iht400Date, LocalDate.now()) < 20) {
            userMessage = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ")"
                    + " needs to be before 20 working days before current date");
        }
    }
}
