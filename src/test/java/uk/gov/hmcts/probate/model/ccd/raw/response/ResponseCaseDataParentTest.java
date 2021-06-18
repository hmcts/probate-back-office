package uk.gov.hmcts.probate.model.ccd.raw.response;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ResponseCaseDataParentTest {

    @Test
    public void shouldApplyParentAttributes() {
        DynamicList reprintDocument =
            DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect =
            DynamicList.builder().value(DynamicListItem.builder().code("solsAmendLegalStatmentSelect").build()).build();

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
            .reprintDocument(reprintDocument).reprintNumberOfCopies("1")
            .solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
            .declarationCheckbox("Yes")
            .ihtGrossValueField("1000").ihtNetValueField("900")
            .numberOfExecutors(1L).numberOfApplicants(2L)
            .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
            .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress")
            .registrySequenceNumber("registrySequenceNumber")
            .deceasedDeathCertificate("deathCertificate")
            .deceasedDiedEngOrWales("Yes")
            .isSolThePrimaryApplicant("Yes")
            .deceasedForeignDeathCertInEnglish("Yes")
            .deceasedForeignDeathCertTranslation("Yes")
            .iht217("Yes")
            .build();

        assertEquals("reprintDocument", responseCaseDataParent.getReprintDocument().getValue().getCode());
        assertEquals("1", responseCaseDataParent.getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect",
            responseCaseDataParent.getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("Yes", responseCaseDataParent.getDeclarationCheckbox());
        assertEquals("Yes", responseCaseDataParent.getIsSolThePrimaryApplicant());
        assertEquals("1000", responseCaseDataParent.getIhtGrossValueField());
        assertEquals("900", responseCaseDataParent.getIhtNetValueField());
        assertEquals(Long.valueOf(1), responseCaseDataParent.getNumberOfExecutors());
        assertEquals(Long.valueOf(2), responseCaseDataParent.getNumberOfApplicants());
        assertEquals("legalDeclarationJson", responseCaseDataParent.getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", responseCaseDataParent.getCheckAnswersSummaryJson());
        assertEquals("registryAddress", responseCaseDataParent.getRegistryAddress());
        assertEquals("registryEmailAddress", responseCaseDataParent.getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", responseCaseDataParent.getRegistrySequenceNumber());
        assertEquals("Yes", responseCaseDataParent.getIht217());
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
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorTrustCorp = new CollectionMember<>(
                new AdditionalExecutorTrustCorps(
                        "Executor forename", 
                        "Executor surname", 
                        "Solicitor"
                ));
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        SolsAddress trustCorpAddress = new SolsAddress(
                "Address Line 1",
                "",
                "",
                "",
                "",
                "POSTCODE",
                "");

        SolsAddress addressOfSucceededFirm = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        SolsAddress addressOfFirmNamedInWill = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .dispenseWithNotice("Yes")
                .dispenseWithNoticeLeaveGiven("No")
                .dispenseWithNoticeOverview("Overview")
                .dispenseWithNoticeSupportingDocs("Supporting docs")
                .titleAndClearingType("TCTTrustCorpResWithApp")
                .trustCorpName("Trust corp name")
                .trustCorpAddress(trustCorpAddress)
                .addressOfSucceededFirm(addressOfSucceededFirm)
                .addressOfFirmNamedInWill(addressOfFirmNamedInWill)
                .furtherEvidenceForApplication("Further evidence")
                .additionalExecutorsTrustCorpList(additionalExecutorsTrustCorpList)
                .lodgementAddress("London")
                .lodgementDate("02-02-2020")
                .build();

        assertEquals("Yes", responseCaseDataParent.getDispenseWithNotice());
        assertEquals("No", responseCaseDataParent.getDispenseWithNoticeLeaveGiven());
        assertEquals("Overview", responseCaseDataParent.getDispenseWithNoticeOverview());
        assertEquals("Supporting docs", responseCaseDataParent.getDispenseWithNoticeSupportingDocs());
        assertEquals("TCTTrustCorpResWithApp", responseCaseDataParent.getTitleAndClearingType());
        assertEquals("Trust corp name", responseCaseDataParent.getTrustCorpName());
        assertEquals(trustCorpAddress, responseCaseDataParent.getTrustCorpAddress());
        assertEquals(addressOfSucceededFirm, responseCaseDataParent.getAddressOfSucceededFirm());
        assertEquals(addressOfFirmNamedInWill, responseCaseDataParent.getAddressOfFirmNamedInWill());
        assertEquals("Further evidence", responseCaseDataParent.getFurtherEvidenceForApplication());
        assertEquals(additionalExecutorsTrustCorpList, responseCaseDataParent.getAdditionalExecutorsTrustCorpList());
        assertEquals("London", responseCaseDataParent.getLodgementAddress());
        assertEquals("02-02-2020", responseCaseDataParent.getLodgementDate());
    }

    @Test
    public void shouldApplyNonTrustCorpOptionAttributes() {
        CollectionMember<AdditionalExecutorPartners> otherPartner = new CollectionMember<>(
                new AdditionalExecutorPartners(
                        "Executor forename",
                        "Executor surname",
                        mock(SolsAddress.class)));
        List<CollectionMember<AdditionalExecutorPartners>> otherPartnersList = new ArrayList<>();
        otherPartnersList.add(otherPartner);

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .dispenseWithNotice("Yes")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .nameOfFirmNamedInWill("Test Solicitor Ltd")
                .otherPartnersApplyingAsExecutors(otherPartnersList)
                .nameOfSucceededFirm("New Firm Ltd")
                .whoSharesInCompanyProfits(Arrays.asList(new String[]{"Partners", "Members"}))
                .morePartnersHoldingPowerReserved("No")
                .build();

        assertEquals("Yes", responseCaseDataParent.getDispenseWithNotice());
        assertEquals("TCTPartSuccPowerRes", responseCaseDataParent.getTitleAndClearingType());
        assertEquals("Test Solicitor Ltd", responseCaseDataParent.getNameOfFirmNamedInWill());
        assertEquals(otherPartnersList, responseCaseDataParent.getOtherPartnersApplyingAsExecutors());
        assertEquals("New Firm Ltd", responseCaseDataParent.getNameOfSucceededFirm());
        assertEquals("Partners", responseCaseDataParent.getWhoSharesInCompanyProfits().get(0));
        assertEquals("Members", responseCaseDataParent.getWhoSharesInCompanyProfits().get(1));
        assertEquals("No", responseCaseDataParent.getMorePartnersHoldingPowerReserved());
    }

    @Test
    public void shouldApplyTrustCorpNoneOfTheseAttributes() {
        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
                .titleAndClearingType("TCTNoT")
                .build();

        assertEquals("TCTNoT", responseCaseDataParent.getTitleAndClearingType());
    }
}
