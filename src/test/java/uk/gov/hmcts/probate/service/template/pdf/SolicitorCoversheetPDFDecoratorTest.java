package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP_WELSH;

class SolicitorCoversheetPDFDecoratorTest {

    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;

    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    @Mock
    private PA14FormBusinessRule pa14FormBusinessRuleMock;
    @Mock
    private PA15FormBusinessRule pa15FormBusinessRuleMock;

    private NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    @Mock
    private PA16FormBusinessRule pa16FormBusinessRuleMock;
    @Mock
    private PA17FormBusinessRule pa17FormBusinessRuleMock;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRuleMock;
    @Mock
    private AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRuleMock;
    @Mock
    private AdmonWillRenunicationRule admonWillRenunicationRuleMock;
    @Mock
    private TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRuleMock;
    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRuleMock;
    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    private ObjectMapper objectMapper;

    private CaseData.CaseDataBuilder caseDataBuilder;

    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingList;
    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplyingRenounced1;

    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplyingRenounced1;

    @BeforeEach
    public void setup() {
        openMocks(this);
        setBusinessRuleMocksApplicable(false);
        objectMapper = new ObjectMapper();
        notApplyingExecutorsMapper = new NotApplyingExecutorsMapper();
        caseExtraDecorator = new CaseExtraDecorator(objectMapper);
        solicitorCoversheetPDFDecorator = new SolicitorCoversheetPDFDecorator(
                caseExtraDecorator,
                pa14FormBusinessRuleMock,
                pa15FormBusinessRuleMock,
                pa16FormBusinessRuleMock,
                pa17FormBusinessRuleMock,
                ihtEstate207BusinessRuleMock,
                dispenseNoticeSupportDocsRuleMock,
                authenticatedTranslationBusinessRuleMock,
                admonWillRenunicationRuleMock,
                notApplyingExecutorsMapper,
                tcResolutionLodgedWithApplicationRuleMock,
                noDocumentsRequiredBusinessRule
        );
        caseDataBuilder = CaseData.builder();
        additionalExecutorsNotApplyingList = new ArrayList<>();
    }

    private void setBusinessRuleMocksApplicable(boolean isApplicable) {
        when(pa14FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(authenticatedTranslationBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(dispenseNoticeSupportDocsRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
    }

    @Test
    void shouldNotProvideAdditionalDecoration() {
        String caseJson = "";
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(caseJson, json);
    }

    @Test
    void shouldProvideNoDocumentsRequiredDecoration() {
        when(noDocumentsRequiredBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"documentsNotRequired\":\"Yes\"}";
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);
        assertNotNull(json);
        assertEquals(extra, json);
    }

    @Test
    void shouldProvidePA14Decoration() {

        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorName()).thenReturn("Executor One");
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorReason())
                .thenReturn(REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE);
        when(additionalExecutorsNotApplyingRenounced1.getValue()).thenReturn(additionalExecutorNotApplyingRenounced1);

        additionalExecutorsNotApplyingList = new ArrayList<>();
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingRenounced1);
        final CaseData caseData = caseDataBuilder.additionalExecutorsNotApplying(
                additionalExecutorsNotApplyingList).build();
        when(pa14FormBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseData);
        assertNotNull(json);
        assertTrue(json.contains("Executor One"));
        assertTrue(json.contains(PA14_FORM_URL));
        assertTrue(json.contains(PA14_FORM_TEXT));
        assertTrue(json.contains(PA14_FORM_TEXT_WELSH));
    }

    @Test
    void shouldProvidePA15Decoration() {
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorName()).thenReturn("Executor One");
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorReason())
                .thenReturn(REASON_FOR_NOT_APPLYING_RENUNCIATION);
        when(additionalExecutorsNotApplyingRenounced1.getValue()).thenReturn(additionalExecutorNotApplyingRenounced1);

        additionalExecutorsNotApplyingList = new ArrayList<>();
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingRenounced1);
        final CaseData caseData = caseDataBuilder.additionalExecutorsNotApplying(
                additionalExecutorsNotApplyingList).build();
        when(pa15FormBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseData);
        assertNotNull(json);
        assertTrue(json.contains("Executor One"));
        assertTrue(json.contains(PA15_FORM_URL));
        assertTrue(json.contains(PA15_FORM_TEXT));
        assertTrue(json.contains(PA15_FORM_TEXT_WELSH));
    }

    @Test
    void shouldProvidePA16Decoration() {
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertNotNull(json);
        assertTrue(json.contains(PA16_FORM_TEXT));
        assertTrue(json.contains(PA16_FORM_TEXT));
        assertTrue(json.contains(PA16_FORM_TEXT_WELSH));
    }

    @Test
    void shouldProvideAdditionalDecorationPA17() {
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertNotNull(json);
        assertTrue(json.contains(PA17_FORM_TEXT));
        assertTrue(json.contains(PA17_FORM_TEXT));
        assertTrue(json.contains(PA17_FORM_TEXT_WELSH));
    }

    @Test
    void shouldProvideIhtEstate207Decoration() {
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock))
                .thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertNotNull(json);
        assertTrue(json.contains(IHT_ESTATE_207_TEXT));
        assertTrue(json.contains(IHT_ESTATE_207_TEXT_WELSH));
    }

    @Test
    void shouldProvideAdmonWillRenunciationDecoration() {
        when(admonWillRenunicationRuleMock.isApplicable(caseDataMock)).thenReturn(true);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertNotNull(json);
        assertTrue(json.contains(ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT));
        assertTrue(json.contains(ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT_WELSH));
        assertTrue(json.contains(ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT));
        assertTrue(json.contains(ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT_WELSH));
    }

    @Test
    void shouldProvideTcResolutionLodgedWithApplicationDecoration() {
        when(tcResolutionLodgedWithApplicationRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertNotNull(json);
        assertTrue(json.contains(TC_RESOLUTION_LODGED_WITH_APP));
        assertTrue(json.contains(TC_RESOLUTION_LODGED_WITH_APP_WELSH));
    }

    @Test
    void shouldProvideAllDecorations() {
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorName()).thenReturn("Executor One");
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorReason())
                .thenReturn(REASON_FOR_NOT_APPLYING_RENUNCIATION);
        when(additionalExecutorsNotApplyingRenounced1.getValue()).thenReturn(additionalExecutorNotApplyingRenounced1);

        additionalExecutorsNotApplyingList = new ArrayList<>();
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingRenounced1);
        final CaseData caseData = caseDataBuilder.additionalExecutorsNotApplying(
                additionalExecutorsNotApplyingList).build();
        when(pa15FormBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        when(pa16FormBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        when(pa17FormBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        when(authenticatedTranslationBusinessRuleMock.isApplicable(caseData)).thenReturn(true);
        when(dispenseNoticeSupportDocsRuleMock.isApplicable(caseData)).thenReturn(true);

        String json = solicitorCoversheetPDFDecorator.decorate(caseData);

        assertNotNull(json);
        assertTrue(json.contains("Executor One"));
        assertTrue(json.contains(PA15_FORM_URL));
        assertTrue(json.contains(PA15_FORM_TEXT));
        assertTrue(json.contains(PA15_FORM_TEXT_WELSH));
        assertTrue(json.contains(PA16_FORM_TEXT));
        assertTrue(json.contains(PA16_FORM_TEXT));
        assertTrue(json.contains(PA16_FORM_TEXT_WELSH));
        assertTrue(json.contains(PA17_FORM_TEXT));
        assertTrue(json.contains(PA17_FORM_TEXT));
        assertTrue(json.contains(PA17_FORM_TEXT_WELSH));
        assertTrue(json.contains(IHT_ESTATE_207_TEXT));
        assertTrue(json.contains(IHT_ESTATE_207_TEXT_WELSH));
        assertTrue(json.contains(AUTHENTICATED_TRANSLATION_WILL_TEXT));
        assertTrue(json.contains(AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH));
        assertTrue(json.contains(DISPENSE_NOTICE_SUPPORT_TEXT));
        assertTrue(json.contains(DISPENSE_NOTICE_SUPPORT_TEXT_WELSH));
        assertTrue(json.contains(AUTHENTICATED_TRANSLATION_WILL_TEXT));
        assertTrue(json.contains(AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH));
    }
}
