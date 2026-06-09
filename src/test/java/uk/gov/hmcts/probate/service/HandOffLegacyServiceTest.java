package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
public class HandOffLegacyServiceTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @InjectMocks
    private HandOffLegacyService handOffLegacyService;

    @Test
    void setReturnFalseIfNoConditionsMet() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(false, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void shouldReturnFalseCaseHandedOffFlagIsBlank() {
        caseDataBuilder
            .caseHandedOffToLegacySite("")
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(false, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorpSdj() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
            .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorp() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
            .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenAppTypeIsSolicitorCaseTypeGopAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenAppTypeIsSolicitorCaseTypeAdmonWillAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenAppTypeIsSolicitorCaseTypeIntestacyAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenAppTypeIsSolicitorCaseTypeGopAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .willAccessOriginal(NO)
            .willAccessNotarial(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenAppTypeIsSolicitorCaseTypeAdmonWillAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .willAccessOriginal(NO)
            .willAccessNotarial(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void caseHandedOffFlagReturnTrueWhenAppTypeIsSolicitorCaseTypeIntestacyAndApplicantRelationshipIsChildAdopted() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .solsApplicantRelationshipToDeceased("ChildAdopted");

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void handedOffFlagReturnTrueWhenAppTypeIsPersonalCaseTypeIntestacyApplicantRelationshipIsAdoptedAndInEngIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(PERSONAL)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .primaryApplicantRelationshipToDeceased("adoptedChild")
            .primaryApplicantAdoptionInEnglandOrWales(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
    }

    @Test
    void handOffReasonSetTrustCorpWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorpSdj() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.TRUST_CORPORATION,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetTrustCorpWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorp() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.TRUST_CORPORATION,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetForeignDomicileWhenAppTypeIsSolicitorCaseTypeGopAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.GOP.getCaseType())
                .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.FOREIGN_DOMICILE,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetForeignDomicileWhenAppTypeIsSolicitorCaseTypeAdmonWillAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
                .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.FOREIGN_DOMICILE,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetForeignDomicileWhenAppTypeIsSolicitorCaseTypeIntestacyAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.INTESTACY.getCaseType())
                .deceasedDomicileInEngWales(NO);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.FOREIGN_DOMICILE,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetForeignWillWhenAppTypeIsSolicitorCaseTypeGopAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.GOP.getCaseType())
                .willAccessOriginal(NO)
                .willAccessNotarial(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.FOREIGN_WILL,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonSetForeignWillWhenAppTypeIsSolicitorCaseTypeAdmonWillAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
                .willAccessOriginal(NO)
                .willAccessNotarial(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.FOREIGN_WILL,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonExtendedIntestacyWhenAppIsSolicitorCaseTypeIntestacyAndApplicantRelationshipIsChildAdopted() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(SOLICITOR)
                .caseType(DocumentCaseType.INTESTACY.getCaseType())
                .solsApplicantRelationshipToDeceased("ChildAdopted");

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.EXTENDED_INTESTACY,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handOffReasonExtendedIntestacyWhenAppIsPersonalCaseTypeIntestacyApplicantRelationshipIsAdoptedAndInEngIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
                .applicationType(PERSONAL)
                .caseType(DocumentCaseType.INTESTACY.getCaseType())
                .primaryApplicantRelationshipToDeceased("adoptedChild")
                .primaryApplicantAdoptionInEnglandOrWales(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        List<CollectionMember<HandoffReason>> handoffReasonsList = handOffLegacyService.setHandoffReason(caseDetails);
        assertEquals(HandoffReasonId.EXTENDED_INTESTACY,
                handoffReasonsList.get(0).getValue().getCaseHandoffReason());
    }

    @Test
    void handoffReasonsListShouldMapAllItemsWhenHandoffReasonsIsNotNull() {
        HandoffReason reason1 = HandoffReason.builder().caseHandoffReason(HandoffReasonId.TRUST_CORPORATION).build();
        HandoffReason reason2 = HandoffReason.builder().caseHandoffReason(HandoffReasonId.FOREIGN_DOMICILE).build();
        uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<HandoffReason> cm1 =
                new uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<>(null, reason1);
        uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<HandoffReason> cm2 =
                new uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<>(null, reason2);

        CaseData caseData = CaseData.builder()
                .boHandoffReasonList(List.of(cm1, cm2))
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        List<CollectionMember<HandoffReason>> result = handOffLegacyService.setHandoffReason(caseDetails);

        assertEquals(2, result.size());
        assertEquals(HandoffReasonId.TRUST_CORPORATION, result.get(0).getValue().getCaseHandoffReason());
        assertEquals(HandoffReasonId.FOREIGN_DOMICILE, result.get(1).getValue().getCaseHandoffReason());
    }

    @Test
    void handoffReasonsListShouldBeEmptyWhenHandoffReasonsIsNull() {
        CaseData caseData = CaseData.builder()
                .boHandoffReasonList(null)
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        List<CollectionMember<HandoffReason>> result = handOffLegacyService.setHandoffReason(caseDetails);

        assertEquals(0, result.size());
    }
}
