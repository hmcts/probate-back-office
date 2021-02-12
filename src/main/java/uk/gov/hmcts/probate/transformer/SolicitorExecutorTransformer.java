package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.*;
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

    public void setPrimaryApplicantFieldsWithSolicitorInfo(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (solicitorExecutorService.isSolicitorExecutor(caseData)) {
            if (solicitorExecutorService.isSolicitorApplying(caseData)) {

                // Solicitor is primary applicant
                addSolicitorAsPrimaryApplicant(caseData, builder);

            } else {

                if (solicitorExecutorService.getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
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

    private void addSolicitorAsPrimaryApplicant(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
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

    public List<CollectionMember<AdditionalExecutorApplying>> setPrimaryApplicantWithExecutorInfo(List<CollectionMember<AdditionalExecutorApplying>>  executorsApplying,
                                                    CaseData caseData,
                                                    ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (caseData.getPrimaryApplicantForenames() == null && !executorsApplying.isEmpty()) {

            AdditionalExecutorApplying tempExec = executorsApplying.get(0).getValue();

            // Remove executor from executorsApplying list
            executorsApplying.remove(0);

            // Add executor to primary applicant fields
            builder
                    .primaryApplicantForenames(tempExec.getApplyingExecutorFirstName())
                    .primaryApplicantSurname(tempExec.getApplyingExecutorLastName())
                    .primaryApplicantAddress(tempExec.getApplyingExecutorAddress())
                    .primaryApplicantIsApplying(YES)
                    .solsPrimaryExecutorNotApplyingReason(null);
        }

        return executorsApplying;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> setExecutorNotApplyingListWithSolicitorInfo( List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying, CaseData caseData) {

        // Transform list
        if (solicitorExecutorService.isSolicitorExecutor(caseData) && NO.equals(caseData.getSolsSolicitorIsApplying())) {
            // Add solicitor to not applying list
            execsNotApplying = solicitorExecutorService.addSolicitorToNotApplyingList(caseData, execsNotApplying);

        } else if (NO.equals(caseData.getSolsSolicitorIsExec()) || solicitorExecutorService.isSolicitorApplying(caseData)) {

            // Remove solicitor from executor lists as they are primary applicant
            execsNotApplying = solicitorExecutorService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        return execsNotApplying;
    }

    public void otherExecutorExistsTransformation(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (solicitorExecutorService.isSolicitorExecutor(caseData) && !solicitorExecutorService.isSolicitorApplying(caseData)) {
            builder.otherExecutorExists(YES);
        }
    }

    public void mapSolicitorExecutorListsToCaseworkerExecutorsLists(CaseData caseData,
                                                                    ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Initialise lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = new ArrayList<>();

        execsNotApplying = setExecutorNotApplyingListWithSolicitorInfo(execsNotApplying, caseData);

        if (caseData.getAdditionalExecutorsTrustCorpList() != null) {
            // Add trust corps executors
            execsApplying.addAll(solicitorExecutorService.mapFromTrustCorpExecutorsToApplyingExecutors(caseData));
        } else if (caseData.getOtherPartnersApplyingAsExecutors() != null) {
            // Add partner executors
            execsApplying.addAll(solicitorExecutorService.mapFromPartnerExecutorsToApplyingExecutors(caseData));
        }

        if (caseData.getPowerReservedExecutorList() != null) {
            // Add power reserved executors
            execsNotApplying.addAll(solicitorExecutorService.mapFromPowerReservedExecutorsToNotApplyingExecutors(caseData));
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
            // Add main solicitor executor list
            execsApplying.addAll(solicitorExecutorService.mapFromSolsAdditionalExecutorListToApplyingExecutors(caseData));
            execsNotApplying.addAll(solicitorExecutorService.mapFromSolsAdditionalExecutorListToNotApplyingExecutors(caseData));
        }

        // Use executor lists to set primary applicant details
        execsApplying = setPrimaryApplicantWithExecutorInfo(execsApplying, caseData, builder);

        // Set builder with values
        builder.additionalExecutorsApplying(execsApplying);
        builder.additionalExecutorsNotApplying(execsNotApplying);
    }

}
