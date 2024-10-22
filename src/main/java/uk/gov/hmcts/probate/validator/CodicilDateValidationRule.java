package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class CodicilDateValidationRule implements ValidationRule {

    private static final String CODICIL_DATE_MUST_BE_IN_THE_PAST = "codicilDateMustBeInThePast";
    private static final String CODICIL_DATE_MUST_BE_IN_THE_PAST_WELSH = "codicilDateMustBeInThePastWelsh";
    private static final String CODICIL_DATE_MUST_BE_AFTER_WILL_DATE = "codicilDateMustBeAfterOriginalWillSignedDate";
    private static final String CODICIL_DATE_MUST_BE_AFTER_WILL_DATE_WELSH
            = "codicilDateMustBeAfterOriginalWillSignedDateWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData)
                .map(this::getErrorCodeForCodicilDateNotInThePast)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }

    private List<String> getErrorCodeForCodicilDateNotInThePast(CCDData ccdData) {
        List<String> allErrorCodes = new ArrayList<>();
        List<CodicilAddedDate> codicilDates = ccdData.getCodicilAddedDateList();
        if (Constants.NO.equals(ccdData.getWillHasCodicils()) || codicilDates == null) {
            return allErrorCodes;
        }
        LocalDate willSignedDate = ccdData.getOriginalWillSignedDate();
        for (CodicilAddedDate date : codicilDates) {
            LocalDate codicilDate = date.getDateCodicilAdded();
            if (codicilDate != null) {
                if (!codicilDate.isBefore(LocalDate.now())
                        && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_IN_THE_PAST)
                        && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_IN_THE_PAST_WELSH)) {
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_IN_THE_PAST);
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_IN_THE_PAST_WELSH);
                }
                if (willSignedDate != null && codicilDate.isBefore(willSignedDate)
                        && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE)
                        && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE_WELSH)) {
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE);
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE_WELSH);
                }
            }
        }
        return allErrorCodes;
    }
}
