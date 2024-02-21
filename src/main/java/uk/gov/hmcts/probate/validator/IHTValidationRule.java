package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@Slf4j
@RequiredArgsConstructor
public class IHTValidationRule implements SolAddDeceasedEstateDetailsValidationRule {

    public static final String IHT_PROBATE_NET_GREATER_THAN_GROSS = "ihtProbateNetGreaterThanGross";
    public static final String IHT_ESTATE_NET_GREATER_THAN_GROSS = "ihtEstateNetGreaterThanGross";
    public static final String IHT_VALUE_VALIDATION = "ihtValueValidation";

    private final BusinessValidationMessageService businessValidationMessageService;
    private static final String REGEX_PATTERN = "^[1-9]\\d*$";

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData.getIht())
                .map(iht -> {
                    List<String> codes = new ArrayList<>();
                    if (iht.getGrossValue() != null) {
                        log.info("Before gross Value {}", iht.getGrossValue());
                        log.info("Before gross Value String {}", iht.getGrossValue().toPlainString());
                    }
                    if ((iht.getGrossValue() != null && !iht.getGrossValue().toPlainString().matches(REGEX_PATTERN))
                            || (iht.getNetValue() != null && !iht.getNetValue().toPlainString().matches(REGEX_PATTERN))
                            || (iht.getIhtEstateGrossValue() != null && !iht.getIhtEstateGrossValue().toPlainString()
                            .matches(REGEX_PATTERN))
                            || (iht.getIhtEstateNetValue() != null && !iht.getIhtEstateNetValue()
                            .toPlainString().matches(REGEX_PATTERN))) {
                        if (iht.getGrossValue() != null) {
                            log.info("gross Value {}", iht.getGrossValue());
                            log.info("gross Value String {}", iht.getGrossValue().toPlainString());
                        }
                        codes.add(IHT_VALUE_VALIDATION);
                    }
                    if (iht.getNetValue() != null && iht.getGrossValue() != null) {
                        if (iht.getNetValue().compareTo(iht.getGrossValue()) > 0) {
                            codes.add(IHT_PROBATE_NET_GREATER_THAN_GROSS);
                        }
                    }

                    if (iht.getIhtEstateNetValue() != null && iht.getIhtEstateGrossValue() != null) {
                        if (iht.getIhtEstateNetValue().compareTo(iht.getIhtEstateGrossValue()) > 0) {
                            codes.add(IHT_ESTATE_NET_GREATER_THAN_GROSS);
                        }
                    }

                    return codes;
                })
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }
}
