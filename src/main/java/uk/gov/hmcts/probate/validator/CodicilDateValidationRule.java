package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
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
    private static final String CODICIL_DATE_MUST_BE_AFTER_WILL_DATE = "codicilDateMustBeAfterOriginalWillSignedDate";

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
        List<CollectionMember<CodicilAddedDate>> codicilDates = ccdData.getCodicilAddedDateList();
        if (Constants.NO.equals(ccdData.getWillHasCodicils()) || codicilDates == null) {
            return new ArrayList<>();
        }
        LocalDate willSignedDate = ccdData.getOriginalWillSignedDate();
        List<String> allErrorCodes = new ArrayList<>();
        for (int i = 0; i < codicilDates.size(); i++) {
            LocalDate codicilDate = codicilDates.get(i).getValue().getDateCodicilAdded();
            if (codicilDate != null) {
                if (codicilDate.compareTo(LocalDate.now()) >= 0
                    && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_IN_THE_PAST)) {
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_IN_THE_PAST);
                }
                if (willSignedDate != null && codicilDate.compareTo(willSignedDate) <= 0
                        && !allErrorCodes.contains(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE)) {
                    allErrorCodes.add(CODICIL_DATE_MUST_BE_AFTER_WILL_DATE);
                }
            }
        }
        return allErrorCodes;
    }
}
