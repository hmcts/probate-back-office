package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.probate.model.caseaccess.DecisionRequest.decisionRequest;
@Slf4j
@RequiredArgsConstructor
@Service
public class PrepareNocService {

    private final AuthTokenGenerator authTokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    public void addRemovedRepresentatives(CaseData caseData) {
        log.info("representative - 1------" );
        List<CollectionMember<RemovedRepresentative>> representatives = caseData.getRemovedRepresentatives();
        log.info("representative - 2-------");
        RemovedRepresentative representative = setRemovedRepresentative(caseData);
        log.info("representative - " + representative);
        log.info("representativessss - " + representatives);
        representatives.add(new CollectionMember<>(null, representative));
        representatives.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(representatives);
    }

    private RemovedRepresentative setRemovedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();
        Organisation organisation = organisationPolicy.getOrganisation();
        log.info("Organisation - " + organisation);
        return RemovedRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .organisationID(organisation.getOrganisationID())
                .solicitorFirstName(caseData.getSolsSOTForenames())
                .solicitorLastName(caseData.getSolsSOTSurname())
                .solicitorEmail(caseData.getSolsSolicitorEmail())
                .organisation(organisation)
                .build();
    }

    public AboutToStartOrSubmitCallbackResponse applyDecision(CallbackRequest callbackRequest, String authorisation) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        return assignCaseAccessClient.applyDecision(
                authorisation,
                authTokenGenerator.generate(),
                decisionRequest(caseDetails)
        );
    }
}