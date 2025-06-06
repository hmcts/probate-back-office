package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Locale.UK;

@Component
@RequiredArgsConstructor
public class IHTFourHundredDateValidationRule implements IHTFourHundredDateRule {

    public static final String IHT_DATE_IS_INVALID = "iht400DateInvalid";
    public static final String IHT_DATE_IS_INVALID_WELSH = "iht400DateInvalidWelsh";
    public static final String IHT_DATE_IS_INVALID2 = "iht400DateInvalid2";
    public static final String IHT_DATE_IS_INVALID2_WELSH = "iht400DateInvalid2Welsh";
    public static final String IHT_DATE_IS_INVALID3 = "iht400DateInvalid3";
    public static final String IHT_DATE_IS_INVALID3_WELSH = "iht400DateInvalid3Welsh";
    public static final String IHT_DATE_IS_IN_FUTURE = "iht400DateIsInFuture";
    public static final String IHT_DATE_IS_IN_FUTURE_WELSH = "iht400DateIsInFutureWelsh";

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
        String[] empty = {};
        String[] args = {caseDetails.getData().convertDate(addBusinessDays(iht400Date, 20))};
        String userMessage;

        if (countBusinessDaysBetween(iht400Date, LocalDate.now()) < 0) {
            userMessage = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_IN_FUTURE, args, UK);
            String error = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_IN_FUTURE_WELSH, args, UK);
            throw new BusinessValidationException(userMessage,
                "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ") needs to be in the past",
                    error);
        }

        if (countBusinessDaysBetween(iht400Date, LocalDate.now()) < 20) {
            userMessage = "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ")"
                + " needs to be before 20 working days before current date";
            String error1 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID, empty, UK);
            String errorWelsh1 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID_WELSH, empty, UK);
            String error2 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID2, args, UK);
            String errorWelsh2 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID2_WELSH, args, UK);
            String error3 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID3, empty, UK);
            String errorWelsh3 = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID3_WELSH, empty, UK);
            throw new BusinessValidationException(error1, userMessage, errorWelsh1, error2, errorWelsh2, error3,
                    errorWelsh3);
        }
    }
}
