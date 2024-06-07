package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.reform.probate.model.ScannedDocument;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private static final String IHT_VALDIATION_ERROR = "IHT Values validation error";
    private static final String SCANNED_DOCUMENT_TYPE_VALDIATION_ERROR = "Scan Document Type validation error";
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
        if (!errorMessages.isEmpty()) {
            throw new OCRMappingException(IHT_VALDIATION_ERROR, errorMessages);
        }
    }

    public static void validateScannedDocumentTypes(List<CollectionMember<ScannedDocument>>
                                                            scannedDocuments,CaseType caseType) {

        List<String> disallowedDocTypesFound =
                scannedDocuments
                        .stream()
                        .filter(collectionMember ->
                                !allowScannedDocumentTypes.containsKey(collectionMember.getValue().getType())
                                        || (allowScannedDocumentTypes.containsKey(collectionMember.getValue().getType()
                                )
                                && !allowScannedDocumentTypes.get(collectionMember.getValue().getType())
                                        .contains(caseType)))
                        .map(collectionMember -> collectionMember.getValue().getType())
                        .collect(toList());

        if (!disallowedDocTypesFound.isEmpty()) {

            String errorMessage = String.format(
                    INVALID_SCANNED_DOCUMENT_TYPE_ERROR,
                    caseType,
                    StringUtils.join(disallowedDocTypesFound, ", ")
            );

            throw new OCRMappingException(SCANNED_DOCUMENT_TYPE_VALDIATION_ERROR, Arrays.asList(errorMessage));
        }
    }
}
