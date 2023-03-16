package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class SolicitorPostcodeValidationRule implements ValidationRule {

    private static final String SOLICITOR_ADDRESS_MISSING_POST_CODE = "solicitorAddressMissingPostcode";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData)
                .map(this::getErrorCodeForSolicitorPostCodeMissing)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }

    private List<String> getErrorCodeForSolicitorPostCodeMissing(CCDData ccdData) {
        List<String> allErrorCodes = new ArrayList<>();
        SolsAddress solicitorAddress = ccdData.getSolicitor().getFirmAddress();
        if ("Solicitor".equals(ccdData.getApplicationType())) {
            if (StringUtils.isEmpty(solicitorAddress.getPostCode())) {
                allErrorCodes.add(SOLICITOR_ADDRESS_MISSING_POST_CODE);
            }
        }
        return allErrorCodes;
    }
}
