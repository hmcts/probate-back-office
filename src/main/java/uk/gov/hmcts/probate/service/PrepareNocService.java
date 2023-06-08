package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationUser;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.OrganisationApi;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.ChangeOrganisationRequest;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.caseaccess.DecisionRequest.decisionRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrepareNocService {

    private final AuthTokenGenerator tokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    private final SaveNocService saveNocService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;
    private final OrganisationApi organisationApi;
    private final OrganisationsRetrievalService organisationsRetrievalService;

    public void addNocDate(CaseData caseData) {
        caseData.setNocPreparedDate(LocalDate.now());
    }

    public void addRepresentatives(uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails details) {
        CaseData caseData = details.getData();
        List<CollectionMember<ChangeOfRepresentative>> representatives = caseData.getChangeOfRepresentatives();
        ChangeOfRepresentative representative = buildRepresentative(caseData);
        representatives.add(new CollectionMember<>(null, representative));
        log.info("Change of Representatives - " + representatives);
        representatives.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(representatives);
        final GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData
                .builder()
                .changeOfRepresentatives(saveNocService.getRepresentatives(representatives))
                .build();
        log.info("Grant of representation data - " + grantOfRepresentationData);
        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, details.getId().toString(),
                grantOfRepresentationData, EventId.APPLY_DECISION,
                securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), "Apply Noc",
                "Apply Noc");
    }

    private ChangeOfRepresentative buildRepresentative(CaseData caseData) {
        RemovedRepresentative removeRepresentative = caseData.getRemovedRepresentative();
        AddedRepresentative addRepresentative = setAddedRepresentative(caseData);
        log.info("Removed Representative - " + removeRepresentative);
        log.info("Added Representative - " + addRepresentative);
        return ChangeOfRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .addedRepresentative(addRepresentative)
                .removedRepresentative(removeRepresentative)
                .build();
    }

    public RemovedRepresentative setRemovedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();

        if (organisationPolicy != null) {
            Organisation organisation = organisationPolicy.getOrganisation();

            RemovedRepresentative removed = RemovedRepresentative.builder()
                    .organisationID(organisation.getOrganisationID())
                    .solicitorFirstName(caseData.getSolsSOTForenames())
                    .solicitorLastName(caseData.getSolsSOTSurname())
                    .solicitorEmail(caseData.getSolsSolicitorEmail())
                    .organisation(organisation)
                    .build();
            caseData.setRemovedRepresentative(removed);
            return removed;
        }
        return null;
    }

    private AddedRepresentative setAddedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();
        Organisation organisation = organisationPolicy.getOrganisation();
        return AddedRepresentative.builder()
                .organisationID(organisation.getOrganisationID())
                .updatedBy("ABC")
                .updatedVia("NOC")
                .build();
    }

    public AboutToStartOrSubmitCallbackResponse applyDecision(CallbackRequest callbackRequest, String authorisation) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Map<String, Object> caseData = caseDetails.getData();
        log.info("Case data Before- " + caseData);
        log.info("change organisation request- " + caseData.get("changeOrganisationRequestField"));
        ChangeOrganisationRequest changeRequest = getChangeOrganisationRequest(caseData);
        log.info("change organisation request after- " + changeRequest);
        List<CollectionMember<ChangeOfRepresentative>> representatives = getChangeOfRepresentations(caseData);
        log.info("Representatives before- " + representatives);
        ChangeOfRepresentative representative = buildChangeOfRepresentative(caseData);
        representatives.add(new CollectionMember<>(null, representative));
        log.info("Change of Representatives - " + representatives);
        representatives.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(representatives);
        OrganisationEntityResponse organisationEntityResponse = organisationsRetrievalService.getOrganisationEntity(
                    caseDetails.getId().toString(), authorisation);
        log.info("Organisation Entity Response - " + organisationEntityResponse);
        OrganisationUser organisationUser =
                getUserDetails(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), changeRequest);
        log.info("org user - " + organisationUser);
        caseData.put("changeOfRepresentatives", representatives);
        caseDetails.getData().putAll(caseData);
        return assignCaseAccessClient.applyDecision(
                authorisation,
                tokenGenerator.generate(),
                decisionRequest(caseDetails)
        );
    }

    private OrganisationUser getUserDetails(SecurityDTO securityDTO, ChangeOrganisationRequest changeRequest) {
        return organisationApi.findUserByEmail(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(), changeRequest.getCreatedBy());

    }

    private ChangeOfRepresentative buildChangeOfRepresentative(Map<String, Object> caseData) {
        RemovedRepresentative removeRepresentative = getRemovedRepresentative(caseData);
        AddedRepresentative addRepresentative = setAddRepresentative(caseData);
        log.info("Removed Representative - " + removeRepresentative);
        log.info("Added Representative - " + addRepresentative);
        return ChangeOfRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .addedRepresentative(addRepresentative)
                .removedRepresentative(removeRepresentative)
                .build();
    }

    private AddedRepresentative setAddRepresentative(Map<String, Object>  caseData) {
        log.info("change organisation request- " + caseData.get("changeOrganisationRequestField"));
        ChangeOrganisationRequest changeRequest = getChangeOrganisationRequest(caseData);
        log.info("change organisation request after- " + changeRequest);
        OrganisationPolicy organisationPolicy = getOrganisationPolicy(caseData);
        Organisation organisation = organisationPolicy.getOrganisation();
        return AddedRepresentative.builder()
                .organisationID(organisation.getOrganisationID())
                .updatedBy(changeRequest.getCreatedBy())
                .updatedVia("NOC")
                .build();
    }

    private ChangeOrganisationRequest getChangeOrganisationRequest(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class);
    }

    public OrganisationPolicy getOrganisationPolicy(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("applicantOrganisationPolicy"), OrganisationPolicy.class);
    }

    private RemovedRepresentative getRemovedRepresentative(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("removedRepresentative"), RemovedRepresentative.class);
    }

    private List<CollectionMember<ChangeOfRepresentative>> getChangeOfRepresentations(Map<String, Object> caseData) {
        Object changeOfRepresentativesValue = caseData.get("changeOfRepresentatives");
        if (changeOfRepresentativesValue == null) {
            log.info("Change of reps - " + changeOfRepresentativesValue);
            return new ArrayList<>();
        }
        return objectMapper.convertValue(caseData.get("changeOfRepresentatives"), List.class);
    }
}
