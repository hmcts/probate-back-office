package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
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
public class CodicilDateInPastRule implements ValidationRule {

    private static final String CODICIL_DATE_MUST_BE_IN_THE_PAST = "codicilDateMustBeInThePast";

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
        List<LocalDate> codicilDates = ccdData.getCodicilAddedDateList();
        if (Constants.NO.equals(ccdData.getWillHasCodicils()) || codicilDates == null) {
            return new ArrayList<>();
        }

        List<String> allErrorCodes = new ArrayList<>();
        for (int i = 0; i < codicilDates.size(); i++) {
            LocalDate codicilDate = codicilDates.get(i);
            if (codicilDate != null && codicilDate.compareTo(LocalDate.now()) >= 0) {
                allErrorCodes.add(CODICIL_DATE_MUST_BE_IN_THE_PAST);
                break;
            }
        }
        return allErrorCodes;
    }
}
