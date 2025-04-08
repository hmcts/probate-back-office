package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.RemovePenceDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorLegalStatementPDFDecorator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
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
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;

@ExtendWith(SpringExtension.class)
class PDFDecoratorServiceTest {

    private PDFDecoratorService pdfDecoratorService;

    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecoratorMock;
    @Mock
    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecoratorMock;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaseExtraDecorator caseExtraDecoratorMock;
    @Mock
    private RemovePenceDecorator removePenceDecoratorMock;
    private String caseExtraJson1;
    private String caseExtraJson2;
    private String combinedCaseExtraJson;
    private String expectedJson1;
    private String caseDetailsJson;

    @BeforeEach
    public void setup() {
        openMocks(this);
        when(objectMapperMock.copy()).thenReturn(objectMapperMock);
        pdfDecoratorService = new PDFDecoratorService(objectMapperMock, solicitorCoversheetPDFDecoratorMock,
            solicitorLegalStatementPDFDecoratorMock, removePenceDecoratorMock, caseExtraDecoratorMock);
        caseDetailsJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
                + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"}}";
        caseExtraJson1 = "{\"showPa16Form\" : \"Yes\",\"pa16FormText\" : \"<PA16FormText>\"}";
        caseExtraJson2 = "{\"grossValue\" : \"123\",\"netValue\" : \"122\"}";
        combinedCaseExtraJson = "{\"showPa16Form\" : \"Yes\",\"pa16FormText\" : \"<PA16FormText>\","
                + "\"grossValue\" : \"123\",\"netValue\" : \"122\"}";
        expectedJson1 = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
                + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{\"showPa16Form\" : \"Yes\","
                + "\"pa16FormText\" : \"<PA16FormText>\",\"grossValue\" : \"123\",\"netValue\" : \"122\"}}";
    }

    @Test
    void shouldNotDecorateForCaveatRequest() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(caveatCallbackRequestMock)).thenReturn(caseDetailsJson);

        String json = pdfDecoratorService.decorate(caveatCallbackRequestMock, SOLICITOR_COVERSHEET);

        String expectedJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{}}";
        assertEquals(expectedJson, json);
    }

    @Test
    void shouldNotDecorateForNonCoversheet() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(caseDetailsJson);

        String json = pdfDecoratorService.decorate(callbackRequestMock, GRANT_COVER);

        String expectedJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{}}";
        assertEquals(expectedJson, json);
    }

    @Test
    void shouldNotDecorateThrowBadRequest() throws JsonProcessingException {
        assertThrows(BadRequestException.class, () -> {
            when(objectMapperMock.writeValueAsString(caveatCallbackRequestMock))
                    .thenThrow(JsonProcessingException.class);

            pdfDecoratorService.decorate(caveatCallbackRequestMock, SOLICITOR_COVERSHEET);
        });
    }

    @Test
    void shouldDecorateSolicitorCoversheet() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(caseDetailsJson);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        String expectedJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{\"showPa16Form\" : \"Yes\","
            + "\"pa16FormText\" : \"<PA16FormText>\"}}";
        when(solicitorCoversheetPDFDecoratorMock.decorate(caseDataMock)).thenReturn(caseExtraJson1);

        String json = pdfDecoratorService.decorate(callbackRequestMock, SOLICITOR_COVERSHEET);

        assertEquals(expectedJson, json);
    }

    @ParameterizedTest
    @MethodSource("legalDocumentTypeStream")
    void shouldDecorateSolicitorLegalStatement(final DocumentType documentType) throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(caseDetailsJson);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(solicitorLegalStatementPDFDecoratorMock.decorate(caseDataMock)).thenReturn(caseExtraJson1);
        when(removePenceDecoratorMock.decorate(caseDataMock, documentType)).thenReturn(caseExtraJson2);
        when(caseExtraDecoratorMock.combineDecorations(caseExtraJson1, caseExtraJson2))
                .thenReturn(combinedCaseExtraJson);

        String json = pdfDecoratorService.decorate(callbackRequestMock, documentType);

        assertEquals(expectedJson1, json);
    }

    @ParameterizedTest
    @MethodSource("grantDocumentTypeStream")
    void shouldDecorateGrantDocuments(final DocumentType documentType) throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(caseDetailsJson);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(removePenceDecoratorMock.decorate(caseDataMock, documentType)).thenReturn(caseExtraJson2);
        String expectedJson2 = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{\"grossValue\" : \"123\","
            + "\"netValue\" : \"122\"}}";

        String json = pdfDecoratorService.decorate(callbackRequestMock, documentType);

        assertEquals(expectedJson2, json);
    }

    private static Stream<DocumentType> grantDocumentTypeStream() {
        return Stream.of(ADMON_WILL_GRANT, ADMON_WILL_GRANT_DRAFT, DIGITAL_GRANT, DIGITAL_GRANT_DRAFT,
                DIGITAL_GRANT_REISSUE, DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT, INTESTACY_GRANT_DRAFT,
                AD_COLLIGENDA_BONA_GRANT, AD_COLLIGENDA_BONA_GRANT_DRAFT);
    }

    private static Stream<DocumentType> legalDocumentTypeStream() {
        return Stream.of(LEGAL_STATEMENT_ADMON, LEGAL_STATEMENT_INTESTACY, LEGAL_STATEMENT_PROBATE,
                LEGAL_STATEMENT_PROBATE_TRUST_CORPS);
    }
}
