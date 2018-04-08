package uk.gov.hmcts.probate.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DobDodValidationRule implements ValidationRule {

    public static final String CODE_DOD_BEFORE_DOB = "dodIsBeforeDob";
    public static final String CODE_DOD_ON_DOB = "dodIsSameAsDob";
    public static final String CODE_DOB_IN_FUTURE = "dobIsInTheFuture";
    public static final String CODE_DOD_IN_FUTURE = "dodIsInTheFuture";

    private final BusinessValidationMessageService businessValidationMessageService;

    @Autowired
    public DobDodValidationRule(BusinessValidationMessageService businessValidationMessageService) {
        this.businessValidationMessageService = businessValidationMessageService;
    }

    @Override
    public List<BusinessValidationError> validate(CCDData ccdData) {

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
