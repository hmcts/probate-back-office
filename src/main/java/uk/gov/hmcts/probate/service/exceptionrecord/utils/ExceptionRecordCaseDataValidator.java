package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_NETESTATEVALUE_GRATER_THAN_ESTATE_GROSS_VALUE;
import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_NETQUALIFYINGVALUE_SHOULDNOTBE_GREATER_THAN_GROSS;

@Slf4j
@Component
public class ExceptionRecordCaseDataValidator {

    private static final String IHT_PROBATE_NET_GREATER_THAN_GROSS =
            "The gross probate value cannot be less than the net probate value";
    private static final String IHT_ESTATE_NET_GREATER_THAN_GROSS =
            "The gross IHT value cannot be less than the net IHT value";
    private static final String IHT_VALDIATION_ERROR = "IHT Values validation error";

    private ExceptionRecordCaseDataValidator() {
    }

    public static void validateIhtValues(GrantOfRepresentationData caseData) {
        List<String> errorMessages = new ArrayList<>();
        if (caseData.getIhtNetValue() != null && caseData.getIhtGrossValue() != null) {
            if (caseData.getIhtNetValue().compareTo(caseData.getIhtGrossValue()) > 0) {
                log.error(IHT_PROBATE_NET_GREATER_THAN_GROSS);
                errorMessages.add(IHT_PROBATE_NET_GREATER_THAN_GROSS);
            }
        }

        if (caseData.getIhtEstateNetValue() != null && caseData.getIhtEstateGrossValue() != null) {
            if (caseData.getIhtEstateNetValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
                log.error(IHT_ESTATE_NET_GREATER_THAN_GROSS);
                errorMessages.add(IHT_ESTATE_NET_GREATER_THAN_GROSS);
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new OCRMappingException(IHT_VALDIATION_ERROR, errorMessages);
        }
        if (caseData.getIhtEstateNetQualifyingValue() != null && caseData.getIhtEstateNetValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateNetValue()) > 0) {
                log.error(IHT_NETQUALIFYINGVALUE_SHOULDNOTBE_GREATER_THAN_GROSS);
                errorMessages.add(IHT_NETQUALIFYINGVALUE_SHOULDNOTBE_GREATER_THAN_GROSS);
            }
        }
        if (caseData.getIhtEstateGrossValue() != null && caseData.getIhtEstateNetQualifyingValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
                log.error(IHT_NETESTATEVALUE_GRATER_THAN_ESTATE_GROSS_VALUE);
                errorMessages.add(IHT_NETESTATEVALUE_GRATER_THAN_ESTATE_GROSS_VALUE);
            }
        }
    }
}
