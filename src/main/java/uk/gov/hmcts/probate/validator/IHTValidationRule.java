package uk.gov.hmcts.probate.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class IHTValidationRule implements ValidationRule {

    public static final String IHT_NET_GREATER_THAN_GROSS = "ihtNetGreaterThanGross";
    public static final String IHT_NET_IS_NULL = "ihtNetIsNull";
    public static final String IHT_GROSS_IS_NULL = "ihtGrossIsNull";

    private final BusinessValidationMessageService businessValidationMessageService;

    @Autowired
    public IHTValidationRule(BusinessValidationMessageService businessValidationMessageService) {
        this.businessValidationMessageService = businessValidationMessageService;
    }

    @Override
    public List<BusinessValidationError> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData.getIht())
            .map(iht -> {
                List<String> codes = new ArrayList<>();

                if (iht.getNetValue() > iht.getGrossValue()) {
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
