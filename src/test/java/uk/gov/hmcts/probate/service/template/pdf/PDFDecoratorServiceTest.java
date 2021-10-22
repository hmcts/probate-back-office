package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;

public class PDFDecoratorServiceTest {

    private PDFDecoratorService pdfDecoratorService;

    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecoratorMock;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
        when(objectMapperMock.copy()).thenReturn(objectMapperMock);
        pdfDecoratorService = new PDFDecoratorService(objectMapperMock, solicitorCoversheetPDFDecoratorMock);
    }

    @Test
    public void shouldNotDecorate() throws JsonProcessingException {
        String caseDetailsJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"}}";
        when(objectMapperMock.writeValueAsString(caveatCallbackRequestMock)).thenReturn(caseDetailsJson);

        String json = pdfDecoratorService.decorate(caveatCallbackRequestMock, SOLICITOR_COVERSHEET);

        String expectedJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{}}";
        assertEquals(expectedJson, json);
    }

    @Test
    public void shouldDecorateSolicitorCoversheet() throws JsonProcessingException {
        String caseDetailsJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"}," 
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"}}";
        String caseExtraJson = "{\"showPa16Form\" : \"Yes\",\"pa16FormText\" : \"<PA16FormText>\"}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(caseDetailsJson);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        String expectedJson = "{\"case_details\":{\"case_data\":{\"solsSolicitorWillSignSOT\":\"Yes\"},"
            + "\"id\":1634732500947999,\"state\":\"SolAppUpdated\"},\"case_extras\":{\"showPa16Form\" : \"Yes\","
            + "\"pa16FormText\" : \"<PA16FormText>\"}}";
        when(solicitorCoversheetPDFDecoratorMock.decorate(caseDataMock)).thenReturn(caseExtraJson);

        String json = pdfDecoratorService.decorate(callbackRequestMock, SOLICITOR_COVERSHEET);

        assertEquals(expectedJson, json);
    }

}