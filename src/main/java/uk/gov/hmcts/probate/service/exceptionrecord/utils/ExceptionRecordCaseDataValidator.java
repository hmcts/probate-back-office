package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.List;

@Slf4j
@Component
public class ExceptionRecordCaseDataValidator {

    private static final String IHT_PROBATE_NET_GREATER_THAN_GROSS =
            "The gross probate value cannot be less than the net probate value";
    private static final String IHT_ESTATE_NET_GREATER_THAN_GROSS =
            "The gross IHT value cannot be less than the net IHT value";
    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE =
            "Net qualifying value can't be greater than the net amount";
    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE =
            "Net qualifying value can't be greater than the gross amount";

    private ExceptionRecordCaseDataValidator() {
    }

    public static void validateIhtValues(GrantOfRepresentationData caseData, List<String> warnings) {
        if (caseData.getIhtNetValue() != null && caseData.getIhtGrossValue() != null) {
            if (caseData.getIhtNetValue().compareTo(caseData.getIhtGrossValue()) > 0) {
                log.error(IHT_PROBATE_NET_GREATER_THAN_GROSS);
                warnings.add(IHT_PROBATE_NET_GREATER_THAN_GROSS);
            }
        }

        if (caseData.getIhtEstateNetValue() != null && caseData.getIhtEstateGrossValue() != null) {
            if (caseData.getIhtEstateNetValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
                log.error(IHT_ESTATE_NET_GREATER_THAN_GROSS);
                warnings.add(IHT_ESTATE_NET_GREATER_THAN_GROSS);
            }
        }

        if (caseData.getIhtEstateNetQualifyingValue() != null && caseData.getIhtEstateNetValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateNetValue()) > 0) {
                log.error(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE);
                warnings.add(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE);
            }
        }
        if (caseData.getIhtEstateGrossValue() != null && caseData.getIhtEstateNetQualifyingValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
                log.error(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE);
                warnings.add(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE);
            }
        }
    }
}
