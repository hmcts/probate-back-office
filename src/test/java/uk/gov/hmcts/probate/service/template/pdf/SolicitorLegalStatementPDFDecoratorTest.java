package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.ApplicantFamilyDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorLegalStatementPDFDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.HALF_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_SIBLING;

class SolicitorLegalStatementPDFDecoratorTest {

    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Mock
    private IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    private ObjectMapper objectMapper;

    private CaseData.CaseDataBuilder caseDataBuilder;


    @BeforeEach
    public void setup() {
        openMocks(this);
        objectMapper = new ObjectMapper();
        caseExtraDecorator = new CaseExtraDecorator(objectMapper);
        solicitorLegalStatementPDFDecorator = new SolicitorLegalStatementPDFDecorator(
                caseExtraDecorator,
                ihtEstateNotCompletedBusinessRule
        );
    }

    @Test
    void shouldReturnSiblingRelationshipForSolicitorWholeBloodSibling() {
        List<CollectionMember<AdditionalExecutorApplying>> executors = new ArrayList<>();
        executors.add(new CollectionMember<>("1", AdditionalExecutorApplying.builder()
                .applyingExecutorName("John Doe")
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased("child").build())
                .build()));
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SIBLING);
        when(caseDataMock.getApplicantSameParentsAsDeceased()).thenReturn(WHOLE_SIBLING);
        when(caseDataMock.getExecutorsApplyingLegalStatement()).thenReturn(executors);

        String result = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertTrue(result.contains("John Doe is the whole blood sibling"));
    }

    @Test
    void shouldReturnSiblingRelationshipForSolicitorHalfBloodSibling() {
        List<CollectionMember<AdditionalExecutorApplying>> executors = new ArrayList<>();
        executors.add(new CollectionMember<>("1", AdditionalExecutorApplying.builder()
                .applyingExecutorName("John Doe")
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased("child").build())
                .build()));
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SIBLING);
        when(caseDataMock.getApplicantSameParentsAsDeceased()).thenReturn(HALF_SIBLING);
        when(caseDataMock.getExecutorsApplyingLegalStatement()).thenReturn(executors);

        String result = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertTrue(result.contains("John Doe is the half blood sibling"));
    }
}
