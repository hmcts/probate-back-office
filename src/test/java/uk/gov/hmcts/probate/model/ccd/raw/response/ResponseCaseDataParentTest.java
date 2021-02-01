package uk.gov.hmcts.probate.model.ccd.raw.response;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

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
    public void shouldApplySolicitorInfoAttributes() {

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .solsForenames("Solicitor Forename")
                .solsSurname("Solicitor Surname")
                .solsSolicitorWillSignSOT("Yes")
                .build();

        assertEquals("Solicitor Forename", responseCaseDataParent.getSolsForenames());
        assertEquals("Solicitor Surname", responseCaseDataParent.getSolsSurname());
        assertEquals("Yes", responseCaseDataParent.getSolsSolicitorWillSignSOT());
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
        assertEquals("Executor name", responseCaseDataParent.getAdditionalExecutorsTrustCorpList().get(0).getValue().getAdditionalExecutorTrustCorpName());
        assertEquals("Solicitor", responseCaseDataParent.getAdditionalExecutorsTrustCorpList().get(0).getValue().getAdditionalExecutorTrustCorpPosition());
        assertEquals("London", responseCaseDataParent.getLodgementAddress());
        assertEquals("02-02-2020", responseCaseDataParent.getLodgementDate());
    }

    @Test
    public void shouldApplyNonTrustCorpOptionAttributes() {
        CollectionMember<OtherPartnerExecutorApplying> otherPartner = new CollectionMember<>(new OtherPartnerExecutorApplying("Jim Smith"));
        List<CollectionMember<OtherPartnerExecutorApplying>> otherPartnersList = new ArrayList<>();
        otherPartnersList.add(otherPartner);

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .dispenseWithNotice("Yes")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .nameOfFirmNamedInWill("Test Solicitor Ltd")
                .otherPartnerExecutorName("Fred Bloggs")
                .anyPartnersApplyingToActAsExecutor("Yes")
                .otherPartnersApplyingAsExecutors(otherPartnersList)
                .nameOfSucceededFirm("New Firm Ltd")
                .build();

        assertEquals("Yes", responseCaseDataParent.getDispenseWithNotice());
        assertEquals("TCTPartSuccPowerRes", responseCaseDataParent.getTitleAndClearingType());
        assertEquals("Test Solicitor Ltd", responseCaseDataParent.getNameOfFirmNamedInWill());
        assertEquals("Fred Bloggs", responseCaseDataParent.getOtherPartnerExecutorName());
        assertEquals("Yes", responseCaseDataParent.getAnyPartnersApplyingToActAsExecutor());
        assertEquals("Jim Smith", responseCaseDataParent.getOtherPartnersApplyingAsExecutors().get(0).getValue().getOtherPartnerExecutorName());
        assertEquals("New Firm Ltd", responseCaseDataParent.getNameOfSucceededFirm());
    }
}
