package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class HandOffLegacyTransformerTest {

    @InjectMocks
    private HandOffLegacyTransformer handOffLegacyTransformer;

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private HandOffLegacyService handOffLegacyService;

    @Test
    void setNoIfCaseHandedOffFlagIsNull() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(false);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(NO, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void setNoIfCaseHandedOffFlagIsBlank() {
        caseDataBuilder
            .caseHandedOffToLegacySite("")
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(false);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(NO, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorpSdj() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
            .applicationType(SOLICITOR);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenApplicationTypeIsSolicitorAndTitleClearingTypeIsTrustCorp() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
            .applicationType(SOLICITOR);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenAppTypeIsSolicitorCaseTypeGopAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .deceasedDomicileInEngWales(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenAppTypeIsSolicitorCaseTypeAdmonWillAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .deceasedDomicileInEngWales(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenAppTypeIsSolicitorCaseTypeIntestacyAndDeceasedDomicileInEngWalesIsNo() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .deceasedDomicileInEngWales(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenAppTypeIsSolicitorCaseTypeGopAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .willAccessOriginal(NO)
            .willAccessNotarial(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagShouldSetToYesWhenAppTypeIsSolicitorCaseTypeAdmonWillAndWillAccessNoAndWillNotarialIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .willAccessOriginal(NO)
            .willAccessNotarial(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagSetToYesWhenAppTypeIsSolicitorCaseTypeIntestacyAndApplicantRelationshipIsChildAdopted() {
        caseDataBuilder
            .caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(SOLICITOR)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .solsApplicantRelationshipToDeceased("ChildAdopted");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void caseHandedOffFlagSetToYesWhenAppTypeIsPersonalCaseTypeIntestacyApplicantRelationshipIsAdoptedAndInEngIsYes() {
        caseDataBuilder.caseHandedOffToLegacySite(null)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .applicationType(PERSONAL)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .primaryApplicantRelationshipToDeceased("adoptedChild")
            .primaryApplicantAdoptionInEnglandOrWales(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(YES, callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void shouldSetHandOffReasonWhenHandOffFlagIsYes() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .applicationType(SOLICITOR);

        CollectionMember<HandoffReason> collectionMember = new CollectionMember<>(null, HandoffReason.builder()
                .caseHandoffReason(HandoffReasonId.TRUST_CORPORATION).build());

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(true);
        when(handOffLegacyService.setHandoffReason(caseDetailsMock)).thenReturn(List.of(collectionMember));
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertEquals(HandoffReasonId.TRUST_CORPORATION,
                callbackRequestMock.getCaseDetails().getData().getBoHandoffReasonList().get(0).getValue()
                        .getCaseHandoffReason());
    }

    @Test
    void shouldNotSetHandOffReasonWhenHandOffFlagIsNo() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(handOffLegacyService.setCaseToHandedOffToLegacySite(caseDetailsMock)).thenReturn(false);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequestMock);

        assertNull(callbackRequestMock.getCaseDetails().getData().getBoHandoffReasonList());
    }

    @Test
    void resetHandOffToLegacySiteShouldSetCaseHandedOffToLegacySiteAndBoHandoffReasonListToNull() {
        caseDataBuilder
                .caseHandedOffToLegacySite(YES)
                .boHandoffReasonList(List.of(new uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<>(null,
                        HandoffReason.builder().caseHandoffReason(HandoffReasonId.TRUST_CORPORATION).build())));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        handOffLegacyTransformer.resetHandOffToLegacySite(callbackRequestMock);

        assertNull(callbackRequestMock.getCaseDetails().getData().getBoHandoffReasonList());
        assertNull(callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }

    @Test
    void resetHandOffToLegacySiteShouldHandleAlreadyNullValues() {
        caseDataBuilder
                .caseHandedOffToLegacySite(null)
                .boHandoffReasonList(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        handOffLegacyTransformer.resetHandOffToLegacySite(callbackRequestMock);

        assertNull(callbackRequestMock.getCaseDetails().getData().getBoHandoffReasonList());
        assertNull(callbackRequestMock.getCaseDetails().getData().getCaseHandedOffToLegacySite());
    }
}