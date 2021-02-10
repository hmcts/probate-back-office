package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_NO;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_YES;

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

    public void setExecutorApplyingListsWithSolicitorInfo(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Initialise lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = caseData.getAdditionalExecutorsApplying() == null ?
                new ArrayList<>() : solicitorExecutorService.mapApplyingAdditionalExecutors(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = caseData.getAdditionalExecutorsNotApplying() == null ?
                new ArrayList<>() : caseData.getAdditionalExecutorsNotApplying();

        // Transform lists
        if (solicitorExecutorService.isSolicitorExecutor(caseData) && NO.equals(caseData.getSolsSolicitorIsApplying())) {

            // Add solicitor to not applying list
            execsNotApplying = solicitorExecutorService.addSolicitorToNotApplyingList(caseData, execsNotApplying);
            execsApplying = solicitorExecutorService.removeSolicitorFromApplyingList(execsApplying);

        } else if (NO.equals(caseData.getSolsSolicitorIsExec()) || solicitorExecutorService.isSolicitorApplying(caseData)) {

            // Remove solicitor from executor lists as they are primary applicant
            execsApplying = solicitorExecutorService.removeSolicitorFromApplyingList(execsApplying);
            execsNotApplying = solicitorExecutorService.removeSolicitorFromNotApplyingList(execsNotApplying);

        }

        builder
                .additionalExecutorsApplying(execsApplying)
                .additionalExecutorsNotApplying(execsNotApplying);
    }

    public void otherExecutorExistsTransformation(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        if (solicitorExecutorService.isSolicitorExecutor(caseData) && !solicitorExecutorService.isSolicitorApplying(caseData)) {
            builder.otherExecutorExists(YES);
        }
    }

//    public void mapFromSolsAdditionalExecutorListToCaseworkerExecutorLists(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
//
//        if (CollectionUtils.isEmpty(caseData.getSolsAdditionalExecutorList())) {
//
//            if (solicitorExecutorService.isSolicitorExecutor(caseData)) {
//                setExecutorApplyingListsWithSolicitorInfo(caseData, builder);
//
//            } else {
//                builder
//                        .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
//                        .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying());
//            }
//
//        } else {
//
//            // Initialise lists
//            List<CollectionMember<AdditionalExecutorApplying>> applyingExec = new ArrayList<>();
//            List<CollectionMember<AdditionalExecutorNotApplying>> notApplyingExec = new ArrayList<>();
//
//            // Populate lists
//            for (CollectionMember<AdditionalExecutor> additionalExec : caseData.getSolsAdditionalExecutorList()) {
//                if (ANSWER_YES.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
//                    applyingExec.add( new CollectionMember<>(additionalExec.getId(),
//                            solicitorExecutorService.buildApplyingAdditionalExecutor(additionalExec.getValue())));
//                } else if (ANSWER_NO.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
//                    notApplyingExec.add( new CollectionMember<>(additionalExec.getId(),
//                            solicitorExecutorService.buildNotApplyingAdditionalExecutor(additionalExec.getValue())));
//                }
//            }
//
//            builder
//                    .additionalExecutorsApplying(applyingExec)
//                    .additionalExecutorsNotApplying(notApplyingExec)
//                    .solsAdditionalExecutorList(EMPTY_LIST);
//        }
//    }

    public void addSolicitorToSolsAdditionalExecList(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {

        // Initialise lists
        List<CollectionMember<AdditionalExecutor>> executorList = caseData.getSolsAdditionalExecutorList();
        List<CollectionMember<AdditionalExecutor>> tempExecsList = executorList == null || executorList.isEmpty() ?
                new ArrayList<>() : new ArrayList<>(executorList);

        // If list does NOT contain an solicitor, then update
        if (!solicitorExecutorService.listContainsSolicitor(tempExecsList)
                && solicitorExecutorService.isSolicitorExecutor(caseData)
                && !solicitorExecutorService.isSolicitorApplying(caseData)) {

            // Add solicitor to list
            tempExecsList = solicitorExecutorService.addSolicitorAsNotApplyingExecutorToList(caseData);

        }

        builder.solsAdditionalExecutorList(tempExecsList);
    }

    public void mapSolicitorExecutorListsToCaseworkerExecutorsLists(CaseData caseData,
                                                                    ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
        // Initialise lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = caseData.getAdditionalExecutorsApplying() == null ?
                new ArrayList<>() : solicitorExecutorService.mapApplyingAdditionalExecutors(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = caseData.getAdditionalExecutorsNotApplying() == null ?
                new ArrayList<>() : caseData.getAdditionalExecutorsNotApplying();

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
            execsNotApplying.addAll(solicitorExecutorService.mapFromPowerReservedExecutorsToNotApplyingExecutors(caseData));
        }

        builder.additionalExecutorsApplying(execsApplying);
        builder.additionalExecutorsNotApplying(execsNotApplying);
    }

}
