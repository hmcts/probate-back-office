package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.NoWillRule;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfirmationResponseServiceTest {

    public static final String CONFIRMATION_BODY = "someBody";
    private ConfirmationResponseService underTest;

    @Mock
    private NoOriginalWillRule noOriginalWillRuleMock;
    @Mock
    private NoWillRule noWillRuleMock;
    @Mock
    private DomicilityRule domicilityRuleMock;
    @Mock
    private ExecutorsRule executorsRuleMock;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private TemplateResponse willBodyTemplateResponseMock;
    @Mock
    private MarkdownSubstitutionService markdownSubstitutionServiceMock;
    @Mock
    private MessageResourceService messageResourceServiceMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        underTest = new ConfirmationResponseService(messageResourceServiceMock, markdownSubstitutionServiceMock,
            noWillRuleMock, noOriginalWillRuleMock, domicilityRuleMock, executorsRuleMock);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(willBodyTemplateResponseMock.getTemplate()).thenReturn(CONFIRMATION_BODY);
    }

    @Test
    public void shouldStopWillConfirmationForDomicility() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(domicilityRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getDomicilityStopConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopDomicilityConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(domicilityRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getDomicilityStopConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(null, afterSubmitCallbackResponse.getConfirmationBody());
    }


    @Test
    public void shouldStopWillConfirmationForNoWill() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopWillConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldStopWillConfirmationForWillNotOriginal() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopWillConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopWillConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopWillConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(null, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldStopExecutorConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getExecutorsStopConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNotStopExecutorConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getExecutorsStopConfirmation(callbackRequestMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(null, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Captor
    private ArgumentCaptor<Map<String, String>> nextStepsKeyValueMap;

    @Test
    public void shouldGetNextStepsConfirmation() {
        Solicitor solicitor = mock(Solicitor.class);
        Deceased deceased = mock(Deceased.class);
        InheritanceTax inheritanceTax = mock(InheritanceTax.class);
        Fee fee = mock(Fee.class);
        CCDData ccdDataMock = mock(CCDData.class);
        LocalDate date = LocalDate.parse("2000-12-31");

        when(ccdDataMock.getDeceased()).thenReturn(deceased);
        when(ccdDataMock.getFee()).thenReturn(fee);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTax);
        when(ccdDataMock.getSolicitor()).thenReturn(solicitor);
        when(ccdDataMock.getSolicitorReference()).thenReturn("ref");
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(date);
        when(solicitor.getFirmName()).thenReturn("Sol Firm Name");
        when(solicitor.getFullname()).thenReturn("Andy Test");
        when(solicitor.getJobRole()).thenReturn("Lawyer");
        when(deceased.getFirstname()).thenReturn("Firstname");
        when(deceased.getLastname()).thenReturn("Lastname");
        when(deceased.getDateOfBirth()).thenReturn(date);
        when(deceased.getDateOfDeath()).thenReturn(date);
        when(inheritanceTax.getFormName()).thenReturn("IHT207");
        when(fee.getPaymentMethod()).thenReturn("Cheque");
        when(fee.getAmount()).thenReturn(BigDecimal.valueOf(100.00));
        when(fee.getAmountInPounds()).thenReturn(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        when(ccdDataMock.getSolsAdditionalInfo()).thenReturn("solsAdditionalInfo");

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("ref", nextStepsValues.get("{{solicitorReference}}"));
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertEquals("Sol Firm Name", nextStepsValues.get("{{solsSolicitorFirmName}}"));
        assertEquals("Andy Test", nextStepsValues.get("{{solicitorName}}"));
        assertEquals("Lawyer", nextStepsValues.get("{{solicitorJobRole}}"));
        assertEquals("Firstname", nextStepsValues.get("{{deceasedFirstname}}"));
        assertEquals("Lastname", nextStepsValues.get("{{deceasedLastname}}"));
        assertEquals("31/12/2000", nextStepsValues.get("{{deceasedDateOfDeath}}"));
        assertEquals("IHT207", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("Cheque", nextStepsValues.get("{{paymentMethod}}"));
        assertEquals("100.00", nextStepsValues.get("{{paymentAmount}}"));
        assertEquals("solsAdditionalInfo", nextStepsValues.get("{{additionalInfo}}"));
    }

}
