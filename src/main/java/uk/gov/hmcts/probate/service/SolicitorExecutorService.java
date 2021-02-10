package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorTrustCorps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class SolicitorExecutorService {

    private static final String SOLICITOR_ID = "solicitor";

    public List<CollectionMember<AdditionalExecutorNotApplying>> addSolicitorToNotApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOLICITOR_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromNotApplyingList(execs);
        }
        updatedExecs.add(getSolicitorNotApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> removeSolicitorFromApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying) {

        if (execsApplying.isEmpty()) return execsApplying;

        return execsApplying.stream()
                .filter(exec -> !SOLICITOR_ID.equals(exec.getId()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> removeSolicitorFromNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying) {

        if (execsNotApplying.isEmpty()) return execsNotApplying;

        return execsNotApplying.stream()
                .filter(exec -> !SOLICITOR_ID.equals(exec.getId()))
                .collect(Collectors.toList());
    }
    
    private CollectionMember<AdditionalExecutorNotApplying> getSolicitorNotApplyingExecutor(CaseData caseData) {
        AdditionalExecutorNotApplying exec = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(caseData.getSolsSOTForenames() + " " + caseData.getSolsSOTSurname())
                .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
                .build();

        return new CollectionMember<>(SOLICITOR_ID, exec);
    }

    public List<CollectionMember<AdditionalExecutor>> addSolicitorAsNotApplyingExecutorToList(CaseData caseData) {

        // Initialise list
        List<CollectionMember<AdditionalExecutor>> execsList = caseData.getSolsAdditionalExecutorList();
        List<CollectionMember<AdditionalExecutor>> tempExecsList = execsList == null || execsList.isEmpty() ?
                new ArrayList<>() : new ArrayList<>(execsList);

        // Add solicitor as executor to list
        CollectionMember<AdditionalExecutor> solicitorExecutor = new CollectionMember<>(SOLICITOR_ID, AdditionalExecutor.builder()
                .additionalExecForenames(caseData.getSolsSOTForenames())
                .additionalExecLastname(caseData.getSolsSOTSurname())
                .additionalExecNameOnWill(NO)
                .additionalApplying(NO)
                .additionalExecReasonNotApplying(caseData.getSolsSolicitorNotApplyingReason())
                .build());
        tempExecsList.add(solicitorExecutor);

        return tempExecsList;
    }

    public boolean listContainsSolicitor(List<CollectionMember<AdditionalExecutor>> executorsList) {
        return executorsList.stream().anyMatch(exec -> SOLICITOR_ID.equalsIgnoreCase(exec.getId()));
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapApplyingAdditionalExecutors(CaseData caseData) {
        return caseData.getAdditionalExecutorsApplying()
                .stream()
                .map(this::buildApplyingAdditionalExecutors)
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorApplying> buildApplyingAdditionalExecutors(CollectionMember<AdditionalExecutorApplying> additionalExecutorApplying) {
        AdditionalExecutorApplying tempExec = additionalExecutorApplying.getValue();

        if (tempExec.getApplyingExecutorName() == null) {
            additionalExecutorApplying.getValue().setApplyingExecutorName(tempExec.getApplyingExecutorFirstName()
                    + " " + tempExec.getApplyingExecutorLastName());
        }

        return additionalExecutorApplying;
    }

    public AdditionalExecutorApplying buildApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorApplying) {
        return AdditionalExecutorApplying.builder()
                .applyingExecutorName(additionalExecutorApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorApplying.getAdditionalExecLastname())
                .applyingExecutorPhoneNumber(null)
                .applyingExecutorEmail(null)
                .applyingExecutorAddress(additionalExecutorApplying.getAdditionalExecAddress())
                .applyingExecutorOtherNames(additionalExecutorApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    public AdditionalExecutorNotApplying buildNotApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorNotApplying) {
        return AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(additionalExecutorNotApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorNotApplying.getAdditionalExecLastname())
                .notApplyingExecutorReason(additionalExecutorNotApplying.getAdditionalExecReasonNotApplying())
                .notApplyingExecutorNameOnWill(additionalExecutorNotApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromTrustCorpExecutorsToApplyingExecutors(List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsList) {
        return trustCorpsList
                .stream()
                .map(this::buildApplyingExecFromTrustCorpExec)
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorApplying> buildApplyingExecFromTrustCorpExec(CollectionMember<AdditionalExecutorTrustCorps> trustCorpExec) {
        AdditionalExecutorTrustCorps tempExec = trustCorpExec.getValue();

        return new CollectionMember<>(trustCorpExec.getId(), AdditionalExecutorApplying.builder()
                .applyingExecutorName(tempExec.getAdditionalExecForenames() + " " + tempExec.getAdditionalExecLastname())
                .applyingExecutorAddress(tempExec.getAdditionalExecAddress())
                .build());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromPartnerExecutorsToApplyingExecutors(List<CollectionMember<AdditionalExecutorPartners>> partnersList) {
        return partnersList
                .stream()
                .map(this::buildApplyingExecFromPartnerExec)
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorApplying> buildApplyingExecFromPartnerExec(CollectionMember<AdditionalExecutorPartners> partnerExec) {
        AdditionalExecutorPartners tempExec = partnerExec.getValue();

        return new CollectionMember<>(partnerExec.getId(), AdditionalExecutorApplying.builder()
                .applyingExecutorName(tempExec.getAdditionalExecForenames() + " " + tempExec.getAdditionalExecLastname())
                .applyingExecutorAddress(tempExec.getAdditionalExecAddress())
                .build());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromPowerReservedExecutorsToNotApplyingExecutors(List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> powerReservedList) {
        return powerReservedList
                .stream()
                .map(this::buildNotApplyingExecFromPowerReservedExec)
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorNotApplying> buildNotApplyingExecFromPowerReservedExec(CollectionMember<AdditionalExecutorNotApplyingPowerReserved> powerReservedExec) {
        AdditionalExecutorNotApplyingPowerReserved tempExec = powerReservedExec.getValue();

        return new CollectionMember<>(powerReservedExec.getId(), AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(tempExec.getNotApplyingExecutorName())
                .notApplyingExecutorReason("PowerReserved")
                .build());
    }

    public boolean isSolicitorExecutor(CaseData caseData) { return YES.equals(caseData.getSolsSolicitorIsExec()); }

    public boolean isSolicitorApplying(CaseData caseData) { return YES.equals(caseData.getSolsSolicitorIsApplying()); }

    public String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }
}

