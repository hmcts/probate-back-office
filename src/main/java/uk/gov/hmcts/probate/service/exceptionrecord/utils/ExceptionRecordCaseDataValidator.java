package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_CHERISHED;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_COVERSHEET;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_FORM;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_PPS_LEGAL_STATEMENT;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_SUPPORTING_DOCUMENTS;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_IHT;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_FORENSIC_SHEETS;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;

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
    private static final String IHT_VALDIATION_ERROR = "IHT Values validation error";
    private static final String INVALID_SCANNED_DOCUMENT_TYPE_ERROR = "Invalid scanned Document Type Error "
            + "for case type '%s': [%s]";

    private static final Map<String, List<CaseType>> allowScannedDocumentTypes =
            Map.of(DOC_TYPE_WILL, singletonList(CaseType.GRANT_OF_REPRESENTATION),
                    DOC_TYPE_IHT, singletonList(CaseType.GRANT_OF_REPRESENTATION),
                    DOC_TYPE_FORENSIC_SHEETS, singletonList(CaseType.GRANT_OF_REPRESENTATION),
                    DOC_TYPE_SUPPORTING_DOCUMENTS, singletonList(CaseType.GRANT_OF_REPRESENTATION),
                    DOC_TYPE_PPS_LEGAL_STATEMENT, singletonList(CaseType.GRANT_OF_REPRESENTATION),
                    DOC_TYPE_COVERSHEET, List.of(CaseType.GRANT_OF_REPRESENTATION, CaseType.CAVEAT),
                    DOC_TYPE_CHERISHED, List.of(CaseType.GRANT_OF_REPRESENTATION, CaseType.CAVEAT),
                    DOC_TYPE_OTHER, List.of(CaseType.GRANT_OF_REPRESENTATION, CaseType.CAVEAT),
                    DOC_TYPE_FORM, List.of(CaseType.GRANT_OF_REPRESENTATION, CaseType.CAVEAT)
            );


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

        if (caseData.getIhtEstateNetQualifyingValue() != null && caseData.getIhtEstateNetValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateNetValue()) > 0) {
                log.error(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE);
                errorMessages.add(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE);
            }
        }
        if (caseData.getIhtEstateGrossValue() != null && caseData.getIhtEstateNetQualifyingValue() != null) {
            if (caseData.getIhtEstateNetQualifyingValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
                log.error(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE);
                errorMessages.add(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE);
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new OCRMappingException(IHT_VALDIATION_ERROR, errorMessages);
        }
    }


    public static void validateInputScannedDocumentTypes(List<InputScannedDoc>
                                                            scannedDocuments,CaseType caseType) {

        List<String> disallowedDocTypesFound =
                scannedDocuments
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(inputScannedDoc ->
                                !allowScannedDocumentTypes.containsKey(inputScannedDoc.type)
                                        || (allowScannedDocumentTypes.containsKey(inputScannedDoc.type
                                )
                                        && !allowScannedDocumentTypes.get(inputScannedDoc.type)
                                        .contains(caseType)))
                        .map(inputScannedDoc -> inputScannedDoc.type)
                        .collect(toList());

        if (!disallowedDocTypesFound.isEmpty()) {

            String errorMessage = String.format(
                    INVALID_SCANNED_DOCUMENT_TYPE_ERROR,
                    caseType,
                    StringUtils.join(disallowedDocTypesFound, ", ")
            );

            throw new OCRMappingException(errorMessage, Arrays.asList(errorMessage));
        }
    }
}
