package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

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
        assertEquals(NO, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
    }

    @Test
    void shouldReturnFalseCaseHandedOffFlagIsBlank() {
        caseDataBuilder
            .caseHandedOffToLegacySite("")
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(false, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
        assertEquals(NO, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorpSdj() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
            .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
    }

    @Test
    void caseHandedOffFlagShouldReturnTrueWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorp() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
            .applicationType(SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
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
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
    }

    @Test
    void caseHandedOffFlagReturnTrueWhenAppTypeIsPersonalCaseTypeIntestacyApplicantRelationshipIsAdoptedAndInEngIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(PERSONAL)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .primaryApplicantRelationshipToDeceased("adoptedChild")
            .primaryApplicantAdoptionInEnglandOrWales(YES);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        assertEquals(true, handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetails));
        assertEquals(YES, handOffLegacyService.getHandOffLegacySiteYesOrNo(caseDetails));
    }
}
