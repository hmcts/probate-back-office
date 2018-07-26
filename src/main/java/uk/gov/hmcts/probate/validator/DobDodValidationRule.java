package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
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
class DobDodValidationRule implements SolicitorCreateValidationRule {

    public static final String CODE_DOD_BEFORE_DOB = "dodIsBeforeDob";
    public static final String CODE_DOD_ON_DOB = "dodIsSameAsDob";
    public static final String CODE_DOB_IN_FUTURE = "dobIsInTheFuture";
    public static final String CODE_DOD_IN_FUTURE = "dodIsInTheFuture";

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {

        return Optional.ofNullable(ccdData.getDeceased())
                .map(deceased -> {
                    List<String> codes = new ArrayList<>();

                    LocalDate dob = deceased.getDateOfBirth();
                    LocalDate dod = deceased.getDateOfDeath();

                    LocalDate now = LocalDate.now();

                    if (dob.isAfter(now)) {
                        codes.add(CODE_DOB_IN_FUTURE);
                    }

                    if (dod.isAfter(now)) {
                        codes.add(CODE_DOD_IN_FUTURE);
                    }

                    if (dod.equals(dob)) {
                        codes.add(CODE_DOD_ON_DOB);
                    }

                    if (dod.isBefore(dob)) {
                        codes.add(CODE_DOD_BEFORE_DOB);
                    }

                    return codes;
                })
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }
}
