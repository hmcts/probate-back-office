package uk.gov.hmcts.probate.transformer;

import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class TransformerUtils {

    public static String getOtherExecutorExists(CaseData caseData) {
        if (ApplicationType.PERSONAL.equals(caseData.getApplicationType())) {
            return caseData.getAdditionalExecutorsApplying() == null || caseData.getAdditionalExecutorsApplying().isEmpty()
                    ? NO : YES;
        } else {
            return caseData.getOtherExecutorExists();
        }
    }

    public static String getPrimaryApplicantHasAlias(CaseData caseData) {
        if (ApplicationType.PERSONAL.equals(caseData.getApplicationType())) {
            return NO;
        } else {
            return caseData.getPrimaryApplicantHasAlias();
        }
    }

    public static String transformMoneyGBPToString(BigDecimal bdValue) {
        return ofNullable(bdValue)
                .map(value -> bdValue.multiply(new BigDecimal(100)))
                .map(BigDecimal::intValue)
                .map(String::valueOf)
                .orElse(null);
    }

    public static String transformToString(BigDecimal bdValue) {
        return ofNullable(bdValue)
                .map(BigDecimal::intValue)
                .map(String::valueOf)
                .orElse(null);
    }

    public static String transformToString(Long longValue) {
        return ofNullable(longValue)
                .map(String::valueOf)
                .orElse(null);
    }

    public static CollectionMember<BulkPrint> buildBulkPrint(String letterId, String templateName) {
        return new CollectionMember<>(null, BulkPrint.builder()
                .sendLetterId(letterId)
                .templateName(templateName)
                .build());
    }

    public static List<CollectionMember<BulkPrint>> appendToBulkPrintCollection(
            CollectionMember<BulkPrint> bulkPrintCollectionMember, CaseData caseData) {
        if (caseData.getBulkPrintId() == null) {
            caseData.setBulkPrintId(Arrays.asList(
                    bulkPrintCollectionMember));

        } else {
            caseData.getBulkPrintId().add(bulkPrintCollectionMember);
        }
        return caseData.getBulkPrintId();
    }

    public static String getTemplateName(List<Document> documents, DocumentType[] documentTypes) {
        String templateName = null;

        for (DocumentType documentType : documentTypes) {
            for (int i = 0; i < documents.size(); i++) {
                if (documents.get(i).getDocumentType().getTemplateName().equals(documentType.getTemplateName())) {
                    templateName = documentType.getTemplateName();
                    break;
                }
            }
        }
        return templateName;
    }

    public static String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }
}
