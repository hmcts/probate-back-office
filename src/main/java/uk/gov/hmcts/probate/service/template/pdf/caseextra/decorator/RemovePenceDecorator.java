package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.FORMAT;
import static uk.gov.hmcts.probate.model.Constants.DIVISOR;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;

@Component
@AllArgsConstructor
public class RemovePenceDecorator {
    private final CaseExtraDecorator caseExtraDecorator;

    public String decorate(CaseData caseData, DocumentType documentType) {
        String decoration = "";
        IhtGrossNetValueCaseExtra ihtGrossNetValueCaseExtra = IhtGrossNetValueCaseExtra.builder()
                .grossValue(truncateValue(caseData.getIhtGrossValue(), documentType))
                .netValue(truncateValue(caseData.getIhtNetValue(), documentType)).build();
        decoration = caseExtraDecorator.decorate(ihtGrossNetValueCaseExtra);
        return decoration;
    }

    private String truncateValue(BigDecimal value, DocumentType documentType) {
        if (grantDocumentTypeStream().anyMatch(documentType::equals)) {
            BigDecimal truncatedValue = value.divide(DIVISOR, RoundingMode.FLOOR);
            return FORMAT.format(truncatedValue);
        }
        return String.valueOf(value.divide(DIVISOR, RoundingMode.FLOOR));
    }

    private static Stream<DocumentType> grantDocumentTypeStream() {
        return Stream.of(ADMON_WILL_GRANT, ADMON_WILL_GRANT_DRAFT, DIGITAL_GRANT, DIGITAL_GRANT_DRAFT,
                DIGITAL_GRANT_REISSUE, DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT, INTESTACY_GRANT_DRAFT,
                AD_COLLIGENDA_BONA_GRANT, AD_COLLIGENDA_BONA_GRANT_DRAFT);
    }
}
