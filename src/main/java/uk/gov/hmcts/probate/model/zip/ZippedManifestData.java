package uk.gov.hmcts.probate.model.zip;

import lombok.Builder;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@Builder
public class ZippedManifestData {
    private final String documentId;
    private final String caseNumber;
    private final String docType;
    private final String docFileType;
    private final String subType;
    private final String caseType;
    private String errorDescription;
    private final String comment;

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]++");
    private static final Pattern LEADING_TRAILING_UNDERSCORE = Pattern.compile("(^_++)|(_++$)");
    public String getDocumentName() {
        return this.caseNumber + "_" + this.docType
                + (this.subType != null ? "_" + formatSubType(this.subType) : "")
                + this.docFileType;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    private String formatSubType(String subType) {
        if (subType == null) {
            return "";
        }
        String sanitised = NON_ALPHANUMERIC.matcher(subType.trim().toLowerCase()).replaceAll("_");
        return LEADING_TRAILING_UNDERSCORE.matcher(sanitised).replaceAll("");
    }
}
