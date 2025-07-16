package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;

@ExtendWith(SpringExtension.class)
class RemovePenceDecoratorTest {

    @InjectMocks
    private RemovePenceDecorator removePenceDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    @ParameterizedTest
    @MethodSource("grantDocumentTypeStream")
    void shouldDecorateWithThousandSeparatorForGrantDocuments(final DocumentType documentType) {
        caseDataMock = CaseData.builder().ihtGrossValue(BigDecimal.valueOf(126456))
                .ihtNetValue(BigDecimal.valueOf(125456)).build();
        String caseExtraJson = "{\"grossValue\" : \"1,264\",\"netValue\" : \"1,254\"}";
        when(caseExtraDecorator.decorate(any(IhtGrossNetValueCaseExtra.class))).thenReturn(caseExtraJson);
        String actual = removePenceDecorator.decorate(caseDataMock, documentType);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldNotDecorateWithThousandSeparatorForLegalDocuments() {
        caseDataMock = CaseData.builder().ihtGrossValue(BigDecimal.valueOf(126456))
                .ihtNetValue(BigDecimal.valueOf(125456)).build();
        String caseExtraJson = "{\"grossValue\" : \"1264\",\"netValue\" : \"1254\"}";
        when(caseExtraDecorator.decorate(any(IhtGrossNetValueCaseExtra.class))).thenReturn(caseExtraJson);
        String actual = removePenceDecorator.decorate(caseDataMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS);
        assertEquals(caseExtraJson, actual);
    }

    private static Stream<DocumentType> grantDocumentTypeStream() {
        return Stream.of(ADMON_WILL_GRANT, ADMON_WILL_GRANT_DRAFT, DIGITAL_GRANT, DIGITAL_GRANT_DRAFT,
                DIGITAL_GRANT_REISSUE, DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT, INTESTACY_GRANT_DRAFT,
                AD_COLLIGENDA_BONA_GRANT, AD_COLLIGENDA_BONA_GRANT_DRAFT);
    }
}
