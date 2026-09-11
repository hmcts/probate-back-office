package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.ApplicantFamilyDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SolicitorLegalStatementPDFDecoratorTest {

    @InjectMocks
    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;
    @Mock
    private CaseData caseDataMock;

    @Test
    void shouldDecorateForIhtEstateNotCompleted() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    void shouldNotDecorate() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("", actual);
    }

    @Test
    void shouldDecorateWithEnglishAndWelshDescriptionsWhenOneExecutorPresent() {
        List<CollectionMember<AdditionalExecutorApplying>> executors = new ArrayList<>();

        AdditionalExecutorApplying executor1 = AdditionalExecutorApplying.builder()
                .applyingExecutorName("John Doe")
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased("child").build())
                .build();
        executors.add(new CollectionMember<>("1", executor1));
        caseDataMock = CaseData.builder().executorsApplyingLegalStatement(executors)
                .solsApplicantRelationshipToDeceased("child").solsWillType("NoWill").build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String caseExtraJson
                = "{\"englishCoApplicantDescriptions\" : \"Joh Doe is the child\",\"welshCoApplicantDescriptions\" : "
                + "\"John Doe yw plentyn\"}";
        when(caseExtraDecorator.decorate(any(IntestacyMultipleApplicantsCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldDecorateWithEnglishAndWelshDescriptionsWhenMoreExecutorPresent() {
        List<CollectionMember<AdditionalExecutorApplying>> executors = new ArrayList<>();

        AdditionalExecutorApplying executor1 = AdditionalExecutorApplying.builder()
                .applyingExecutorName("John Doe")
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased("child").build())
                .build();
        AdditionalExecutorApplying executor2 = AdditionalExecutorApplying.builder()
                .applyingExecutorName("John Doe1")
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased("child").build())
                .build();
        executors.add(new CollectionMember<>("1", executor1));
        executors.add(new CollectionMember<>("2", executor2));
        caseDataMock = CaseData.builder().executorsApplyingLegalStatement(executors)
                .solsApplicantRelationshipToDeceased("child").solsWillType("NoWill").build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String caseExtraJson
                = "{\"englishCoApplicantDescriptions\" : \"Joh Doe is the child\",\"welshCoApplicantDescriptions\" : "
                + "\"John Doe yw plentyn\"}";
        when(caseExtraDecorator.decorate(any(IntestacyMultipleApplicantsCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldDecorateWithEmptyDescriptionsWhenExecutorsListIsNull() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getExecutorsApplyingLegalStatement()).thenReturn(null);
        when(caseExtraDecorator.decorate(any(IntestacyMultipleApplicantsCaseExtra.class)))
                .thenReturn("decoratedJson");
        when(caseExtraDecorator.combineDecorations("", "decoratedJson"))
                .thenReturn("decoratedJson");
        when(caseExtraDecorator.decorate(any())).thenReturn("decoratedJson");

        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);

        assertEquals("decoratedJson", actual);
    }
}
