package uk.gov.hmcts.probate.model.ccd.raw.response;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ResponseCaseDataParentTest {

    @Test
    public void shouldApplyAttributes() {
        DynamicList reprintDocument = DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect = DynamicList.builder().value(DynamicListItem.builder().code("solsAmendLegalStatmentSelect").build()).build();

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .reprintDocument(reprintDocument).reprintNumberOfCopies("1").solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
                .declarationCheckbox("Yes")
                .ihtGrossValueField("1000").ihtNetValueField("900")
                .numberOfExecutors(1L).numberOfApplicants(2L)
                .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
                .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress").registrySequenceNumber("registrySequenceNumber")
                .build();

        assertEquals("reprintDocument", responseCaseDataParent.getReprintDocument().getValue().getCode());
        assertEquals("1", responseCaseDataParent.getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect", responseCaseDataParent.getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("Yes", responseCaseDataParent.getDeclarationCheckbox());
        assertEquals("1000", responseCaseDataParent.getIhtGrossValueField());
        assertEquals("900", responseCaseDataParent.getIhtNetValueField());
        assertEquals(Long.valueOf(1), responseCaseDataParent.getNumberOfExecutors());
        assertEquals(Long.valueOf(2), responseCaseDataParent.getNumberOfApplicants());
        assertEquals("legalDeclarationJson", responseCaseDataParent.getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", responseCaseDataParent.getCheckAnswersSummaryJson());
        assertEquals("registryAddress", responseCaseDataParent.getRegistryAddress());
        assertEquals("registryEmailAddress", responseCaseDataParent.getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", responseCaseDataParent.getRegistrySequenceNumber());
    }

    @Test
    public void shouldApplyDeathCertAttributes() {

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .deceasedDeathCertificate("deathCertificate")
                .deceasedDiedEngOrWales("Yes")
                .deceasedForeignDeathCertInEnglish("Yes")
                .deceasedForeignDeathCertTranslation("Yes")
                .build();

        assertEquals("deathCertificate", responseCaseDataParent.getDeceasedDeathCertificate());
        assertEquals("Yes", responseCaseDataParent.getDeceasedDiedEngOrWales());
        assertEquals("Yes", responseCaseDataParent.getDeceasedForeignDeathCertInEnglish());
        assertEquals("Yes", responseCaseDataParent.getDeceasedForeignDeathCertTranslation());

    }

    @Test
    public void shouldApplyTrustCorpAttributes() {

        CollectionMember<AdditionalExecutorTrustCorp> additionalExecutorTrustCorp = new CollectionMember<>(new AdditionalExecutorTrustCorp("Executor name", "Solicitor"));
        List<CollectionMember<AdditionalExecutorTrustCorp>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .dispenseWithNotice("Yes")
                .titleAndClearingType("TCTTrustCorpResWithApp")
                .trustCorpName("Trust corp name")
                .actingTrustCorpName("Acting trust corp name")
                .positionInTrustCorp("Solicitor")
                .additionalExecutorsTrustCorp("Yes")
                .additionalExecutorsTrustCorpList(additionalExecutorsTrustCorpList)
                .lodgementAddress("London")
                .lodgementDate("02-02-2020")
                .build();

        assertEquals("Yes", responseCaseDataParent.getDispenseWithNotice());
        assertEquals("TCTTrustCorpResWithApp", responseCaseDataParent.getTitleAndClearingType());
        assertEquals("Trust corp name", responseCaseDataParent.getTrustCorpName());
        assertEquals("Acting trust corp name", responseCaseDataParent.getActingTrustCorpName());
        assertEquals("Solicitor", responseCaseDataParent.getPositionInTrustCorp());
        assertEquals("Yes", responseCaseDataParent.getAdditionalExecutorsTrustCorp());
        assertEquals("Executor name", responseCaseDataParent.getAdditionalExecutorsTrustCorpList().get(0).getValue().getOtherActingForTrustCorpName());
        assertEquals("Solicitor", responseCaseDataParent.getAdditionalExecutorsTrustCorpList().get(0).getValue().getOtherActingForTrustCorpPosition());
        assertEquals("London", responseCaseDataParent.getLodgementAddress());
        assertEquals("02-02-2020", responseCaseDataParent.getLodgementDate());

    }
}