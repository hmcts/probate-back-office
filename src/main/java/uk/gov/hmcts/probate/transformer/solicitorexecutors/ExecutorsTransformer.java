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

import static uk.gov.hmcts.probate.model.Constants.NO;
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
     * dispenseWithNoticeOtherExecsList, solsAdditionalExecutorList, and solicitor information fields
     */
    public void mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
            CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Get executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        // Populate primary applicant fields
        if (shouldSetPrimaryApplicantFieldsWithExecInfo(execsApplying, caseData)) {
            AdditionalExecutorApplying tempExec = execsApplying.get(0).getValue();
            execsApplying.remove(0);
            mapExecutorToPrimaryApplicantFields(tempExec, builder);
        }

        // Set builder with lists
        builder
                .additionalExecutorsApplying(execsApplying)
                .additionalExecutorsNotApplying(execsNotApplying);
    }

    /**
     * Set solsIdentifiedApplyingExecs and solsIdentifiedNotApplyingExecs with names of executors.
     * Get executor names from solicitor executor lists.
     * These names will be displayed in solicitor journey.
     */
    public void mapSolicitorExecutorFieldsToExecutorNamesLists(
            CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        // Format exec lists into strings
        String execsApplyingNames = FormattingService.createExecsApplyingNames(execsApplying);
        String execsNotApplyingNames = FormattingService.createExecsNotApplyingNames(execsNotApplying);

        // Set builder with exec strings
        builder.solsIdentifiedApplyingExecs(execsApplyingNames);
        builder.solsIdentifiedNotApplyingExecs(execsNotApplyingNames);
    }

    public List<CollectionMember<AdditionalExecutorApplying>> createCaseworkerApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                caseData.getAdditionalExecutorsApplying() == null
                        || caseData.getAdditionalExecutorsApplying().isEmpty()
                        ? new ArrayList<>() : new ArrayList<>(caseData.getAdditionalExecutorsApplying());

        mapSolicitorExecutorApplyingListsToCaseworkerApplyingList(execsApplying, caseData);
        execsApplying = setExecutorApplyingListWithSolicitorInfo(execsApplying, caseData);

        return execsApplying;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> createCaseworkerNotApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                caseData.getAdditionalExecutorsNotApplying() == null
                        || caseData.getAdditionalExecutorsNotApplying().isEmpty()
                        ? new ArrayList<>() : new ArrayList<>(caseData.getAdditionalExecutorsNotApplying());

        mapSolicitorExecutorNotApplyingListsToCaseworkerNotApplyingList(execsNotApplying, caseData);
        execsNotApplying = setExecutorNotApplyingListWithSolicitorInfo(execsNotApplying, caseData);

        return execsNotApplying;
    }

    private void mapSolicitorExecutorApplyingListsToCaseworkerApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {

        if (caseData.getAdditionalExecutorsTrustCorpList() != null) {
            // Add trust corps executors
            execsApplying.addAll(executorListMapperService.mapFromTrustCorpExecutorsToApplyingExecutors(caseData));
        } else if (caseData.getOtherPartnersApplyingAsExecutors() != null) {
            // Add partner executors
            execsApplying.addAll(executorListMapperService.mapFromPartnerExecutorsToApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
            // Add main solicitor executor list
            execsApplying.addAll(executorListMapperService
                    .mapFromSolsAdditionalExecutorListToApplyingExecutors(caseData));
        }

    }

    private void mapSolicitorExecutorNotApplyingListsToCaseworkerNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        if (caseData.getDispenseWithNoticeOtherExecsList() != null) {
            // Add power reserved executors
            execsNotApplying.addAll(executorListMapperService
                    .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
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

        } else if (!isSolicitorExecutor(caseData) || !isSolicitorApplying(caseData)) {

            // Remove solicitor from applying executor list
            execsApplying = executorListMapperService.removeSolicitorFromApplyingList(execsApplying);

        }

        return execsApplying;
    }

    private List<CollectionMember<AdditionalExecutorNotApplying>> setExecutorNotApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (!isSolicitorApplying(caseData)) {

            // Add solicitor to not applying list
            execsNotApplying = executorListMapperService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else if (isSolicitorApplying(caseData)) {

            // Remove solicitor from not applying executor list
            execsNotApplying = executorListMapperService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        return execsNotApplying;
    }

    // Remove the solicitor executor lists from the response data.
    public void nullSolicitorExecutorLists(ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .solsAdditionalExecutorList(null)
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(null);
    }

    private void mapExecutorToPrimaryApplicantFields(
            AdditionalExecutorApplying exec, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .primaryApplicantForenames(exec.getApplyingExecutorFirstName())
                .primaryApplicantSurname(exec.getApplyingExecutorLastName())
                .primaryApplicantEmailAddress(exec.getApplyingExecutorEmail())
                .primaryApplicantPhoneNumber(exec.getApplyingExecutorPhoneNumber())
                .primaryApplicantAddress(exec.getApplyingExecutorAddress())
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(NO)
                .primaryApplicantIsApplying(YES)
                .solsPrimaryExecutorNotApplyingReason(null);
    }

    public void setFieldsIfSolicitorIsNotExecutor(CaseData caseData,
                                                  ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (!isSolicitorExecutor(caseData)) {
            builder
                    .solsSolicitorIsApplying(NO)
                    .solsSolicitorNotApplyingReason(null);
        }
    }

    protected boolean isSolicitorExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    protected boolean isSolicitorApplying(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsApplying());
    }

    private boolean shouldSetPrimaryApplicantFieldsWithExecInfo(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {
        return caseData.getPrimaryApplicantForenames() == null && !execsApplying.isEmpty();
    }

}
