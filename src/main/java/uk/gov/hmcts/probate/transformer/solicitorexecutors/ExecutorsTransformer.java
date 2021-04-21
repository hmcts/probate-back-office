package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.NON_TRUST_PTNR_TITLE_CLEARING_TYPES;
import static uk.gov.hmcts.probate.model.Constants.TRUST_CORP_TITLE_CLEARING_TYPES;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@Slf4j
@AllArgsConstructor
@Primary
public class ExecutorsTransformer {

    protected final ExecutorListMapperService executorListMapperService;

    /**
     * Set caseworker executor fields with solicitor journey fields.
     * Caseworker executor fields: additionalExecutorsApplying, additionalExecutorsNotApplying, and primary applicant
     * fields
     * Solicitor executor fields are: additionalExecutorsTrustCorpList, otherPartnersApplyingAsExecutors,
     * dispenseWithNoticeOtherExecsList, solsAdditionalExecutorList, and solicitor information fields.
     * Note that we have allowed the mutation of some fields of the Request data, in order to make life simpler as
     * CallbackResponSeTransformer was called from everywhere, whereas we just want to transform the sol journey
     * executor lists once and once only, when they complete the application. So it is easier to amend the request
     * data when the completion event happens.
     * We should probably review this down the line and see if we can improve,and amend to a less mutative pattern.
     */
    protected void mapSolicitorExecutorFieldsToCaseworkerExecutorFields(CaseData caseData) {

        // Get executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        caseData.setAdditionalExecutorsApplying(execsApplying);
        caseData.setAdditionalExecutorsNotApplying(execsNotApplying);
        mapPrimaryApplicantFields(caseData);
    }



    // Only ever call this once, when we map 1st execApplying to primary applicant forenames
    // Precondition - execsApplying has solicitor added as a temporary item, at position 0
    public void mapPrimaryApplicantFields(CaseData caseData) {
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                caseData.getAdditionalExecutorsApplying();

        // Populate primary applicant fields
        if (shouldSetPrimaryApplicantFieldsWithExecInfo(caseData)) {
            AdditionalExecutorApplying tempExec = execsApplying.get(0).getValue();
            // For a caseworker,
            // this will remove the first executor they added (if the aplicant is applying) -
            // This is odd quirky behaviour but is as designed
            execsApplying.remove(0);
            mapExecutorToPrimaryApplicantFields(tempExec, caseData);
        }
    }



    /**
     * Set solsIdentifiedApplyingExecs and solsIdentifiedNotApplyingExecs with names of executors.
     * Get executor names from solicitor executor lists.
     * These names will be displayed in solicitor journey.
     */
    public void mapSolicitorExecutorFieldsToExecutorNamesLists(
            CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                createCaseworkerApplyingList(caseData);

        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        execsApplying = setExecutorApplyingListWithSolicitorInfo(execsApplying, caseData);
        execsNotApplying = setExecutorNotApplyingListWithSolicitorInfo(execsNotApplying, caseData);

        // Format exec lists into strings
        String execsApplyingNames = FormattingService.createExecsApplyingNames(execsApplying);
        String execsNotApplyingNames = FormattingService.createExecsNotApplyingNames(execsNotApplying);

        // Set builder with exec strings
        builder.solsIdentifiedApplyingExecs(execsApplyingNames);
        builder.solsIdentifiedNotApplyingExecs(execsNotApplyingNames);

        // Set the copies of this data needed to get round ccd limitations
        builder.solsIdentifiedApplyingExecsCcdCopy(execsApplyingNames);
        builder.solsIdentifiedNotApplyingExecsCcdCopy(execsNotApplyingNames);
    }

    public List<CollectionMember<AdditionalExecutorApplying>> createCaseworkerApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = cloneExecsApplying(caseData);

        mapSolicitorExecutorApplyingListsToCaseworkerApplyingList(execsApplying, caseData);
        execsApplying = setExecutorApplyingListWithSolicitorInfo(execsApplying, caseData);

        return execsApplying;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> createCaseworkerNotApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = cloneExecsNotApplying(caseData);

        mapSolicitorExecutorNotApplyingListsToCaseworkerNotApplyingList(execsNotApplying, caseData);
        execsNotApplying = setExecutorNotApplyingListWithSolicitorInfo(execsNotApplying, caseData);

        return execsNotApplying;
    }

    private void mapSolicitorExecutorApplyingListsToCaseworkerApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {

        final String titleClearingType = caseData.getTitleAndClearingType();
        if (TRUST_CORP_TITLE_CLEARING_TYPES.contains(titleClearingType)
                && YES.equals(caseData.getAnyOtherApplyingPartnersTrustCorp())
                && caseData.getAdditionalExecutorsTrustCorpList() != null
                && !caseData.getAdditionalExecutorsTrustCorpList().isEmpty()) {
            // Add trust corps executors
            execsApplying.addAll(executorListMapperService.mapFromTrustCorpExecutorsToApplyingExecutors(caseData));
        } else if (NON_TRUST_PTNR_TITLE_CLEARING_TYPES.contains(titleClearingType)
                && YES.equals(caseData.getAnyOtherApplyingPartners())
                && caseData.getOtherPartnersApplyingAsExecutors() != null
                && !caseData.getOtherPartnersApplyingAsExecutors().isEmpty()) {
            // Add partner executors
            execsApplying.addAll(executorListMapperService.mapFromPartnerExecutorsToApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null
                && !caseData.getSolsAdditionalExecutorList().isEmpty()) {
            // Add main solicitor executor list
            execsApplying.addAll(executorListMapperService
                    .mapFromSolsAdditionalExecutorListToApplyingExecutors(caseData));
        }
    }

    private void mapSolicitorExecutorNotApplyingListsToCaseworkerNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        if (caseData.getDispenseWithNoticeOtherExecsList() != null
            && !caseData.getDispenseWithNoticeOtherExecsList().isEmpty()) {
            // Add power reserved executors
            execsNotApplying.addAll(executorListMapperService
                    .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null
                && !caseData.getSolsAdditionalExecutorList().isEmpty()) {
            // Add main solicitor executor list
            execsNotApplying.addAll(executorListMapperService
                    .mapFromSolsAdditionalExecsToNotApplyingExecutors(caseData));
        }

    }

    public List<CollectionMember<AdditionalExecutorApplying>> setExecutorApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {

        // Transform list
        if (isSolicitorApplying(caseData)) {

            // Add solicitor to applying list
            execsApplying = executorListMapperService.addSolicitorToApplyingList(caseData, execsApplying);

        } else {

            // Remove solicitor from applying executor list
            execsApplying = executorListMapperService.removeSolicitorFromApplyingList(execsApplying);
        }

        return execsApplying;
    }

    private List<CollectionMember<AdditionalExecutorNotApplying>> setExecutorNotApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (!isSolicitorApplying(caseData) && isSolicitorNamedInWillAsAnExecutor(caseData)) {

            // Add solicitor to not applying list
            execsNotApplying = executorListMapperService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else {

            // Remove solicitor from not applying executor list
            execsNotApplying = executorListMapperService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        return execsNotApplying;
    }

    // Clear the solicitor executor lists (on solicitor completion)
    public void clearSolicitorExecutorLists(CaseData caseData) {
        Optional.ofNullable(caseData.getSolsAdditionalExecutorList()).ifPresent(l -> l.clear());
        Optional.ofNullable(caseData.getAdditionalExecutorsTrustCorpList()).ifPresent(l -> l.clear());
        Optional.ofNullable(caseData.getOtherPartnersApplyingAsExecutors()).ifPresent(l -> l.clear());
        Optional.ofNullable(caseData.getDispenseWithNoticeOtherExecsList()).ifPresent(l -> l.clear());
    }

    // Note - mutates the request data!
    protected void mapExecutorToPrimaryApplicantFields(
            AdditionalExecutorApplying exec, CaseData caseData) {
        caseData.setPrimaryApplicantForenames(exec.getApplyingExecutorFirstName());
        caseData.setPrimaryApplicantSurname(exec.getApplyingExecutorLastName());
        caseData.setPrimaryApplicantEmailAddress(exec.getApplyingExecutorEmail());
        caseData.setPrimaryApplicantPhoneNumber(exec.getApplyingExecutorPhoneNumber());
        caseData.setPrimaryApplicantAddress(exec.getApplyingExecutorAddress());
        caseData.setPrimaryApplicantAlias(null);
        caseData.setPrimaryApplicantHasAlias(NO);
        caseData.setPrimaryApplicantIsApplying(YES);
        caseData.setSolsPrimaryExecutorNotApplyingReason(null);
    }

    public void setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(CaseData caseData) {
        if (!isSolicitorNamedInWillAsAnExecutor(caseData)) {
            caseData.setSolsSolicitorNotApplyingReason(null);
        }
    }

    protected boolean isSolicitorNamedInWillAsAnExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    protected boolean isSolicitorApplying(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsApplying());
    }

    protected List<CollectionMember<AdditionalExecutorApplying>> cloneExecsApplying(CaseData caseData) {

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        if (caseData.getAdditionalExecutorsApplying() == null || caseData.getAdditionalExecutorsApplying().isEmpty()) {
            return execsApplying;
        }

        List<CollectionMember<AdditionalExecutorApplying>> cdExecsApplying = caseData.getAdditionalExecutorsApplying();
        for (int i = 0; i < cdExecsApplying.size(); i++) {
            execsApplying.add(new CollectionMember<>(cdExecsApplying.get(i).getId(),
                    cdExecsApplying.get(i).getValue().clone()));
        }
        return execsApplying;
    }

    protected List<CollectionMember<AdditionalExecutorNotApplying>> cloneExecsNotApplying(CaseData caseData) {

        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = new ArrayList<>();
        if (caseData.getAdditionalExecutorsNotApplying() == null
                || caseData.getAdditionalExecutorsNotApplying().isEmpty()) {
            return execsNotApplying;
        }

        List<CollectionMember<AdditionalExecutorNotApplying>> cdExecsNotApplying =
                caseData.getAdditionalExecutorsNotApplying();
        for (int i = 0; i < cdExecsNotApplying.size(); i++) {
            execsNotApplying.add(new CollectionMember<>(cdExecsNotApplying.get(i).getId(),
                    cdExecsNotApplying.get(i).getValue().clone()));
        }
        return execsNotApplying;
    }

    private boolean shouldSetPrimaryApplicantFieldsWithExecInfo(CaseData caseData) {

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                caseData.getAdditionalExecutorsApplying();

        return execsApplying != null && !execsApplying.isEmpty()
                && (YES.equals(caseData.getSolsSolicitorIsApplying()));
    }

}
