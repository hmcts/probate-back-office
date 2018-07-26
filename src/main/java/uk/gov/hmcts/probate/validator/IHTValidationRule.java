package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
class IHTValidationRule implements SolAddDeceasedEstateDetailsValidationRule {

    public static final String IHT_NET_GREATER_THAN_GROSS = "ihtNetGreaterThanGross";
    public static final String IHT_NET_IS_NULL = "ihtNetIsNull";
    public static final String IHT_GROSS_IS_NULL = "ihtGrossIsNull";

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData.getIht())
                .map(iht -> {
                    List<String> codes = new ArrayList<>();

                    if (iht.getNetValue().compareTo(iht.getGrossValue()) > 0) {
                        codes.add(IHT_NET_GREATER_THAN_GROSS);
                    }

                    return codes;
                })
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }
}
