package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Service
@Slf4j
@AllArgsConstructor
public class SolicitorExecutorTransformer {

    private final SolicitorExecutorService solicitorExecutorService;

    public void setPrimaryApplicantFieldsWithSolicitorInfo(CaseData caseData,
                                                           ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (isSolicitorExecutor(caseData)) {
            if (isSolicitorApplying(caseData)) {

                // Solicitor is primary applicant
                addSolicitorAsPrimaryApplicant(caseData, builder);

            } else {

                if (getSolsSOTName(caseData.getSolsSOTForenames(),
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
        if (isSolicitorExecutor(caseData) && !isSolicitorApplying(caseData)) {
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

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        // Populate primary applicant fields
        if (shouldSetPrimaryApplicantFieldsWithExecInfo(execsApplying, caseData)) {
            AdditionalExecutorApplying tempExec = execsApplying.get(0).getValue();
            execsApplying.remove(0);
            mapExecutorToPrimaryApplicantFields(tempExec, builder);
        }

        // Set builder with values
        builder.additionalExecutorsApplying(execsApplying);
        builder.additionalExecutorsNotApplying(execsNotApplying);
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
            execsApplying.addAll(solicitorExecutorService.mapFromTrustCorpExecutorsToApplyingExecutors(caseData));
        } else if (caseData.getOtherPartnersApplyingAsExecutors() != null) {
            // Add partner executors
            execsApplying.addAll(solicitorExecutorService.mapFromPartnerExecutorsToApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
            // Add main solicitor executor list
            execsApplying.addAll(solicitorExecutorService
                    .mapFromSolsAdditionalExecutorListToApplyingExecutors(caseData));
        }

    }

    private void mapSolicitorExecutorNotApplyingListsToCaseworkerNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        if (caseData.getDispenseWithNoticeOtherExecsList() != null) {
            // Add power reserved executors
            execsNotApplying.addAll(solicitorExecutorService
                    .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
            // Add main solicitor executor list
            execsNotApplying.addAll(solicitorExecutorService
                    .mapFromSolsAdditionalExecsToNotApplyingExecutors(caseData));
        }

    }

    private List<CollectionMember<AdditionalExecutorNotApplying>> setExecutorNotApplyingListWithSolicitorInfo(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (NO.equals(caseData.getSolsSolicitorIsExec()) || NO.equals(caseData.getSolsSolicitorIsApplying())) {

            // Add solicitor to not applying list
            execsNotApplying = solicitorExecutorService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else if (isSolicitorApplying(caseData)) {

            // Remove solicitor from executor lists as they are primary applicant
            execsNotApplying = solicitorExecutorService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        return execsNotApplying;
    }

    private boolean shouldSetPrimaryApplicantFieldsWithExecInfo(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying, CaseData caseData) {
        return caseData.getPrimaryApplicantForenames() == null && !execsApplying.isEmpty()
                && !isSolicitorExecutor(caseData) && !isSolicitorApplying(caseData);
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

    private String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }

}
