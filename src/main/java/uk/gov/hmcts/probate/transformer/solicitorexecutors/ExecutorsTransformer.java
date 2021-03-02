package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Service
@Slf4j
@AllArgsConstructor
public class ExecutorsTransformer {

    private final ExecutorListMapperService executorListMapperService;

    public void setPrimaryApplicantFieldsWithSolicitorInfo(CaseData caseData,
                                                           ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (isSolicitorExecutor(caseData)) {
            if (isSolicitorApplying(caseData)) {

                // Solicitor is primary applicant
                addSolicitorAsPrimaryApplicant(caseData, builder);

            } else {

                if (FormattingService.getSolsSOTName(caseData.getSolsSOTForenames(),
                        caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
                    removeSolicitorAsPrimaryApplicant(builder);
                }

                if (caseData.getSolsSolicitorIsApplying() == null) {
                    builder
                            .solsPrimaryExecutorNotApplyingReason(null);
                }
            }
        } else {
            builder
                    .solsSolicitorIsApplying(NO)
                    .solsSolicitorNotApplyingReason(null);
        }

    }

    private void addSolicitorAsPrimaryApplicant(CaseData caseData,
                                                ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .primaryApplicantForenames(caseData.getSolsSOTForenames())
                .primaryApplicantSurname(caseData.getSolsSOTSurname())
                .primaryApplicantPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                .primaryApplicantEmailAddress(caseData.getSolsSolicitorEmail())
                .primaryApplicantAddress(caseData.getSolsSolicitorAddress())
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(NO)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(null)
                .solsPrimaryExecutorNotApplyingReason(null);
    }

    private void removeSolicitorAsPrimaryApplicant(ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .primaryApplicantForenames(null)
                .primaryApplicantSurname(null)
                .primaryApplicantPhoneNumber(null)
                .primaryApplicantEmailAddress(null)
                .primaryApplicantAddress(null)
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(null)
                .primaryApplicantIsApplying(null)
                .solsPrimaryExecutorNotApplyingReason(null);
    }

    public void otherExecutorExistsTransformation(
            CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (isSolicitorExecutor(caseData) && !isSolicitorApplying(caseData)
                && !otherExecutorExistsIsSetNo(caseData)) {
            builder.otherExecutorExists(YES);
        }
    }

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
        builder.additionalExecutorsApplying(execsApplying);
        builder.additionalExecutorsNotApplying(execsNotApplying);
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

        // Add primary applicant to list
        if (isSolicitorExecutor(caseData) && isSolicitorApplying(caseData)) {
            execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
        }

        // Format exec lists into strings
        String execsApplyingNames = FormattingService.createExecsApplyingNames(execsApplying);
        String execsNotApplyingNames = FormattingService.createExecsNotApplyingNames(execsNotApplying);

        // Set builder with exec strings
        builder.solsIdentifiedApplyingExecs(execsApplyingNames);
        builder.solsIdentifiedNotApplyingExecs(execsNotApplyingNames);
    }

    /**
     * Todo check if this is a good place for this
     */
    public List<CollectionMember<AdditionalExecutorApplying>> mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
            CaseData caseData) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);

        // Add primary applicant to list
        if (isSolicitorExecutor(caseData) && isSolicitorApplying(caseData)) {
            execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
        } else if (caseData.isPrimaryApplicantApplying()) {
            execsApplying.add(executorListMapperService.mapFromPrimaryApplicantToApplyingExecutor(caseData));
        }

        return execsApplying;
    }


    public List<CollectionMember<AdditionalExecutorApplying>> createCaseworkerApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying =
                caseData.getAdditionalExecutorsApplying() == null
                        || caseData.getAdditionalExecutorsApplying().isEmpty()
                        ? new ArrayList<>() : caseData.getAdditionalExecutorsApplying();

        mapSolicitorExecutorApplyingListsToCaseworkerApplyingList(execsApplying, caseData);

        return execsApplying;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> createCaseworkerNotApplyingList(CaseData caseData) {

        // Initialise executor lists
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                caseData.getAdditionalExecutorsNotApplying() == null
                        || caseData.getAdditionalExecutorsNotApplying().isEmpty()
                        ? new ArrayList<>() : caseData.getAdditionalExecutorsNotApplying();

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

    private  List<CollectionMember<AdditionalExecutorNotApplying>>  setExecutorNotApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (NO.equals(caseData.getSolsSolicitorIsExec()) || NO.equals(caseData.getSolsSolicitorIsApplying())) {

            // Add solicitor to not applying list
            execsNotApplying = executorListMapperService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else if (isSolicitorApplying(caseData)) {

            // Remove solicitor from executor lists as they are primary applicant
            execsNotApplying = executorListMapperService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        return execsNotApplying;
    }


    private void mapExecutorToPrimaryApplicantFields(
            AdditionalExecutorApplying exec, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .primaryApplicantForenames(exec.getApplyingExecutorFirstName())
                .primaryApplicantSurname(exec.getApplyingExecutorLastName())
                .primaryApplicantAddress(exec.getApplyingExecutorAddress())
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(NO)
                .primaryApplicantIsApplying(YES)
                .solsPrimaryExecutorNotApplyingReason(null);
    }

    private boolean isSolicitorExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    private boolean isSolicitorApplying(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsApplying());
    }

    private boolean otherExecutorExistsIsSetNo(CaseData caseData) {
        return NO.equals(caseData.getOtherExecutorExists());
    }

    private boolean shouldSetPrimaryApplicantFieldsWithExecInfo(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {
        return caseData.getPrimaryApplicantForenames() == null && !execsApplying.isEmpty()
                && !isSolicitorExecutor(caseData) && !isSolicitorApplying(caseData);
    }

}
