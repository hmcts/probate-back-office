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
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorTypeService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_LAY;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_PROFESSIONAL;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@Slf4j
@AllArgsConstructor
@Primary
public class ExecutorsTransformer {

    protected final ExecutorListMapperService executorListMapperService;
    protected final ExecutorTypeService executorTypeService;

    public void setPrimaryApplicantFieldsWithSolicitorInfo(CaseData caseData,
                                                           ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // If solicitor is an executor named in will
        if (executorTypeService.isSolicitorExecutorNamedOnWill(caseData)) {
            // If solicitor is applying as a named executor
            if (isSolicitorNamedOnWillApplying(caseData)) {

                // Solicitor is the primary applicant
                addSolicitorAsPrimaryApplicant(caseData, builder);

            // If solicitor is not applying as named executor
            } else {

                // Todo fix this with solicitor is main applicant toggle. this is incomplete as does not account for scenario of solicitor also amending name
                // And the solicitor is the primary applicant
                if (FormattingService.getSolsSOTName(caseData.getSolsSOTForenames(),
                        caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
                    // Remove the solicitor as the primary applicant
                    removeSolicitorAsPrimaryApplicant(builder);
                }

                if (caseData.getSolsSolicitorIsApplying() == null) {
                    builder
                            .solsPrimaryExecutorNotApplyingReason(null);
                }
            }
        } else {
            // If solicitor is applying as part of a title and clearing group
            if (isSolicitorPartOfTitleAndClearingGroup(caseData)) {
                // Solicitor is primary applicant
                addSolicitorAsPrimaryApplicant(caseData, builder);
                // todo set is solicitor main applicant to yes
                // Set solicitor executor type
                builder
                        .primaryApplicantType(executorTypeService.solicitorExecutorType(caseData));
            } else {
                removeSolicitorAsPrimaryApplicant(builder);
                builder
                        .primaryApplicantType(EXECUTOR_TYPE_LAY);
            }

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
        if (executorTypeService.isSolicitorExecutorNamedOnWill(caseData) && !isSolicitorNamedOnWillApplying(caseData)
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

        // Add solicitor to list
        if ((executorTypeService.isSolicitorExecutorNamedOnWill(caseData) && isSolicitorNamedOnWillApplying(caseData))
                || isSolicitorPartOfTitleAndClearingGroup(caseData)) {
            execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
        }

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

    private  List<CollectionMember<AdditionalExecutorNotApplying>>  setExecutorNotApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (executorTypeService.isSolicitorExecutorNamedOnWill(caseData)
                && NO.equals(caseData.getSolsSolicitorIsApplying())) {

            // Add solicitor to not applying list
            execsNotApplying = executorListMapperService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else if (NO.equals(caseData.getSolsSolicitorIsExec()) || isSolicitorNamedOnWillApplying(caseData)) {

            // Remove solicitor from not applying list
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
                .primaryApplicantAddress(exec.getApplyingExecutorAddress())
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(NO)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantType(exec.getApplyingExecutorType())
                .solsPrimaryExecutorNotApplyingReason(null);
    }


    protected boolean isSolicitorNamedOnWillApplying(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsApplying());
    }

    private boolean isSolicitorPartOfTitleAndClearingGroup(CaseData caseData) {
        return YES.equals(caseData.getSolTitleAndClearingExecutor());
    }

    private boolean otherExecutorExistsIsSetNo(CaseData caseData) {
        return NO.equals(caseData.getOtherExecutorExists());
    }

    private boolean shouldSetPrimaryApplicantFieldsWithExecInfo(
            // Todo use is solicitor main applicant
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {
        return caseData.getPrimaryApplicantForenames() == null && !execsApplying.isEmpty()
                && ((!executorTypeService.isSolicitorExecutorNamedOnWill(caseData)
                && !isSolicitorNamedOnWillApplying(caseData))
                || NO.equals(caseData.getSolTitleAndClearingExecutor()));
    }

}