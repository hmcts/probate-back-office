package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorApplicationCompletionTransformer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.Constants.CASE_PRINTED_NAME;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final SolicitorApplicationCompletionTransformer solicitorApplicationCompletionTransformer;
    private final ResetCaseDataTransformer resetCaseDataTransformer;
    private final LegalStatementExecutorTransformer legalStatementExecutorTransformer;
    private final EvidenceHandledTransformer evidenceHandledTransformer;
    private final AttachDocumentsTransformer attachDocumentsTransformer;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private static final String IHT400 = "IHT400";
    private static final String IHT205 = "IHT205";
    private static final String PAPERFORM = "PaperForm";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public void transformForSolicitorApplicationCompletion(CallbackRequest callbackRequest) {

        final var caseData = callbackRequest.getCaseDetails().getData();
        resetCaseDataTransformer.resetExecutorLists(caseData);
        solicitorApplicationCompletionTransformer.setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseData);
        solicitorApplicationCompletionTransformer
                .mapSolicitorExecutorFieldsOnCompletion(caseData);

        // Remove the solicitor exec lists. Will not be needed now mapped onto caseworker exec lists.
        solicitorApplicationCompletionTransformer.clearSolicitorExecutorLists(caseData);
    }

    public void transformForSolicitorApplicationCompletion(CallbackRequest callbackRequest,
                                                           BigDecimal totalAmount) {

        transformForSolicitorApplicationCompletion(callbackRequest);
        solicitorApplicationCompletionTransformer.setFieldsOnServiceRequest(callbackRequest.getCaseDetails(),
                totalAmount);
    }


    public void transformCaseDataForValidateProbate(CallbackRequest callbackRequest) {
        final var caseDetails = callbackRequest.getCaseDetails();
        final var caseData = caseDetails.getData();

        solicitorApplicationCompletionTransformer.clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(
                caseDetails);

        solicitorApplicationCompletionTransformer.clearAdditionalExecutorWhenUpdatingApplicantDetails(caseDetails);
        resetCaseDataTransformer.resetExecutorLists(caseData);
        solicitorApplicationCompletionTransformer.setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseData);
        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnAppDetailsComplete(caseData);
        solicitorApplicationCompletionTransformer.eraseCodicilAddedDateIfWillHasNoCodicils(caseData);
    }

    public void transformCaseDataForValidateIntestacy(CallbackRequest callbackRequest) {
        final var caseDetails = callbackRequest.getCaseDetails();
        final var caseData = caseDetails.getData();

        solicitorApplicationCompletionTransformer.clearAdditionalExecutorWhenUpdatingApplicantDetails(caseDetails);
        solicitorApplicationCompletionTransformer.setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseData);
        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnAppDetailsComplete(caseData);
    }

    public void transformCaseDataForValidateAdmon(CallbackRequest callbackRequest) {
        final var caseData = callbackRequest.getCaseDetails().getData();
        legalStatementExecutorTransformer.formatFields(caseData);
        solicitorApplicationCompletionTransformer.eraseCodicilAddedDateIfWillHasNoCodicils(caseData);
    }


    public void transformCaseDataForLegalStatementRegeneration(CallbackRequest callbackRequest) {
        final var caseData = callbackRequest.getCaseDetails().getData();

        // we don't really need to do this, as the temp lists prior to sol journey completion should
        // be empty by this stage, however it makes functional testing a lot simpler to
        // always invoke this method, so we can simulate completion before legal statement generation
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                solicitorApplicationCompletionTransformer.createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                solicitorApplicationCompletionTransformer.createCaseworkerNotApplyingList(caseData);

        solicitorApplicationCompletionTransformer.formatFields(caseData);
        solicitorApplicationCompletionTransformer.createLegalStatementExecutorLists(execsApplying,
                execsNotApplying, caseData);
    }

    public void transformCaseDataForSolicitorExecutorNames(CallbackRequest callbackRequest) {
        final var caseData = callbackRequest.getCaseDetails().getData();
        resetCaseDataTransformer.resetExecutorLists(caseData);
    }

    public void transformCaseDataForEvidenceHandled(CallbackRequest callbackRequest) {
        if (CASE_PRINTED_NAME.equals(callbackRequest.getCaseDetails().getState())) {
            evidenceHandledTransformer.updateEvidenceHandled(callbackRequest.getCaseDetails().getData());
        }
    }

    public void setApplicationSubmittedDateForPA(CaseDetails caseDetails) {
        caseDetails.getData().setApplicationSubmittedDate(LocalDate.now().format(dateTimeFormatter));
    }

    public void transformCaseDataForEvidenceHandledForManualCreateByCW(CallbackRequest callbackRequest) {
        if (CASE_PRINTED_NAME.equals(callbackRequest.getCaseDetails().getState())) {
            evidenceHandledTransformer.updateEvidenceHandledToNo(callbackRequest.getCaseDetails().getData());
        }
    }

    public void transformCaseDataForEvidenceHandledForCreateBulkscan(CallbackRequest callbackRequest) {
        if (CASE_PRINTED_NAME.equals(callbackRequest.getCaseDetails().getState())) {
            evidenceHandledTransformer.updateEvidenceHandledToNo(callbackRequest.getCaseDetails().getData());
        }
    }

    public void transformCaseDataForAttachDocuments(CallbackRequest callbackRequest) {
        if (CASE_PRINTED_NAME.equals(callbackRequest.getCaseDetails().getState())) {
            attachDocumentsTransformer.updateAttachDocuments(callbackRequest.getCaseDetails().getData());
        }
    }

    public void transformCaseDataForDocsReceivedNotificationSent(CallbackRequest callbackRequest) {
        attachDocumentsTransformer.updateDocsReceivedNotificationSent(callbackRequest.getCaseDetails().getData());
    }

    public void transformIhtFormCaseDataByDeceasedDOD(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        if (dateOfDeathIsOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath())) {
            resetIhtFormId(caseData);
        } else {
            resetIhtFormEstate(caseData);
        }
    }

    public void transformFormCaseData(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        if (dateOfDeathIsOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath())) {
            if (caseData.getIhtFormId() != null && YES.equals(caseData.getIhtFormEstateValuesCompleted())) {
                resetIhtFormId(caseData);
                resetExceptedEstateFields(caseData);
                if (!IHT400.equals(caseData.getIhtFormEstate())) {
                    resetHmrcLetterId(caseData);
                }
            } else if (NO.equals(caseData.getIhtFormEstateValuesCompleted())) {
                resetIhtFormAndHmrcLetter(caseData);
            } else if (caseData.getIhtFormEstate() != null && !caseData.getIhtFormEstate().equals("NA")
                    && YES.equals(caseData.getIhtFormEstateValuesCompleted())) {
                resetExceptedEstateFields(caseData);
            }
        } else {
            resetIhtFormEstateCompleted(caseData);
            if (caseData.getIhtFormEstate() != null) {
                resetIhtFormEstate(caseData);
                if (!IHT400.equals(caseData.getIhtFormId())) {
                    resetHmrcLetterId(caseData);
                }
                if (!IHT205.equals(caseData.getIhtFormId())) {
                    resetIht217(caseData);
                }
            }
        }
    }

    private void resetIhtFormId(CaseData caseData) {
        caseData.setIhtFormId(null);
        resetIht217(caseData);
    }

    private void resetHmrcLetterId(CaseData caseData) {
        caseData.setHmrcLetterId(null);
    }

    private void resetIhtFormEstate(CaseData caseData) {
        caseData.setIhtFormEstate(null);
    }

    private void resetIhtFormAndHmrcLetter(CaseData caseData) {
        resetIhtFormEstate(caseData);
        resetIhtFormId(caseData);
        resetHmrcLetterId(caseData);
    }

    private void resetIhtFormEstateCompleted(CaseData caseData) {
        caseData.setIhtFormEstateValuesCompleted(null);
        resetExceptedEstateFields(caseData);
    }

    private void resetIht217(CaseData caseData) {
        caseData.setIht217(null);
    }

    private void resetExceptedEstateFields(CaseData caseData) {
        caseData.setIhtEstateGrossValue(null);
        caseData.setIhtEstateNetValue(null);
        caseData.setIhtEstateNetQualifyingValue(null);
        caseData.setDeceasedHadLateSpouseOrCivilPartner(null);
        caseData.setIhtUnusedAllowanceClaimed(null);
    }

    private boolean dateOfDeathIsOnOrAfterSwitchDate(LocalDate dateOfDeath) {
        return exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(dateOfDeath);
    }

    public void transformCaseDataForPaperForm(CallbackRequest callbackRequest) {
        final var caseData = callbackRequest.getCaseDetails().getData();
        caseData.setChannelChoice(PAPERFORM);
    }
}
