package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.INTESTACY;


class EvidenceHandledTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();
    @InjectMocks
    private EvidenceHandledTransformer evidenceHandledTransformer;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSetEvidenceHandledNoForBulkscanAndManualCreateByCW() {
        evidenceHandledTransformer.updateEvidenceHandledToNo(caseDataMock);
        verify(caseDataMock).setEvidenceHandled(NO);
    }

    @Test
    void shouldDefaultEvidenceHandledNullForPP() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);

        CaseData caseData = caseDataBuilder.build();
        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }

    @Test
    void shouldDefaultEvidenceHandledNullForPA() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        CaseData caseData = caseDataBuilder.build();
        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNoForPPNoDocsRequiredRuleApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR);
        CaseData caseData = caseDataBuilder.build();
        when(noDocumentsRequiredBusinessRule.isApplicable(caseData)).thenReturn(true);

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertEquals(NO, caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNullForPPNoDocsRequiredRuleNotApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR);
        CaseData caseData = caseDataBuilder.build();
        when(noDocumentsRequiredBusinessRule.isApplicable(caseData)).thenReturn(false);

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNoForPAIntestacyNoDocsRequiredRuleApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.PERSONAL)
                .caseType(INTESTACY.getName())
                .primaryApplicantNotRequiredToSendDocuments(YES);
        CaseData caseData = caseDataBuilder.build();

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertEquals(NO, caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNullForPANotIntestacyNoDocsRequiredRuleApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantNotRequiredToSendDocuments(YES);
        CaseData caseData = caseDataBuilder.build();

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNullForPAIntestacyNoDocsRequiredRuleNotApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.PERSONAL)
                .caseType(INTESTACY.getName())
                .primaryApplicantNotRequiredToSendDocuments(NO);
        CaseData caseData = caseDataBuilder.build();

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }

    @Test
    void shouldSetEvidenceHandledNullForPANotIntestacyNoDocsRequiredRuleNotApplicable() {
        caseDataBuilder
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantNotRequiredToSendDocuments(NO);
        CaseData caseData = caseDataBuilder.build();

        evidenceHandledTransformer.updateEvidenceHandled(caseData);
        assertNull(caseData.getEvidenceHandled());
    }
}
