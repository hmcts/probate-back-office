package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.FindUsersByOrganisation;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.caseaccess.SolicitorUser;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.OrganisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.caseaccess.DecisionRequest.decisionRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrepareNocService {

    private final AuthTokenGenerator tokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;
    private final OrganisationApi organisationApi;

    public void addNocDate(CaseData caseData) {
        caseData.setNocPreparedDate(LocalDate.now());
    }

    public RemovedRepresentative setRemovedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();

        if (organisationPolicy != null & organisationPolicy.getOrganisation() != null) {
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

    public AboutToStartOrSubmitCallbackResponse applyDecision(CallbackRequest callbackRequest, String authorisation) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Map<String, Object> caseData = caseDetails.getData();
        ChangeOrganisationRequest changeOrganisationRequest = getChangeOrganisationRequest(caseData);
        log.info("change organisation request" + changeOrganisationRequest);
        List<CollectionMember<ChangeOfRepresentative>> representatives = getChangeOfRepresentations(caseData);
        log.info("Change of Representatives before- " + representatives);
        ChangeOfRepresentative representative = buildChangeOfRepresentative(caseData);
        representatives.add(new CollectionMember<>(null, representative));
        log.info("Change of Representatives after- " + representatives);
        Collections.sort(representatives, new CollectionMemberComparator());
        log.info("List after reverse- " + representatives);
        getNewSolicitorDetails(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                        changeOrganisationRequest, caseData, caseDetails.getId().toString());
        SolsAddress solsAddress =
                getNewSolicitorAddress(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                        changeOrganisationRequest.getOrganisationToAdd().getOrganisationID(),
                        caseData, caseDetails.getId().toString());
        log.info("case data before putting to caseDetails- " + caseData);
        caseData.put("changeOfRepresentatives", representatives);
        caseData.put("solsSolicitorAddress", solsAddress);
        log.info("case data- " + caseData);
        caseDetails.getData().putAll(caseData);
        return assignCaseAccessClient.applyDecision(
                authorisation,
                tokenGenerator.generate(),
                decisionRequest(caseDetails)
        );
    }
    static class CollectionMemberComparator<T extends Comparable<T>> implements Comparator<CollectionMember<T>> {
        public int compare(CollectionMember<T> o1, CollectionMember<T> o2) {
            log.info("o1 {} : o2 {}- ", o1,o2);
            LocalDateTime dt1 = ((ChangeOfRepresentative)o1.getValue()).getAddedDateTime();
            log.info("dt1 {} ", dt1);
            LocalDateTime dt2 = ((ChangeOfRepresentative)o2.getValue()).getAddedDateTime();
            log.info("dt2 {} ", dt2);
            return dt1.compareTo(dt2);
        }
    }

    public SolsAddress getNewSolicitorAddress(SecurityDTO securityDTO, String orgId,
                                               Map<String, Object> caseData, String caseId) {
        try {
            log.info("Get OrganisationEntityResponse for caseId {}", caseId);
            OrganisationEntityResponse organisationResponse =
                    organisationApi.findOrganisationByOrgId(securityDTO.getAuthorisation(),
                            securityDTO.getServiceAuthorisation(), orgId);

            log.info("Found OrganisationEntityResponse for caseId {}, OrganisationEntityResponse {}", caseId,
                    organisationResponse);
            return convertSolicitorAddress(organisationResponse, caseData);
        } catch (Exception e) {
            log.error("Exception when looking up OrganisationEntityResponse for case {} for exception {}",
                    caseId, e.getMessage());
        }
        log.info("No OrganisationEntityResponse for caseId {}", caseId);
        return null;
    }

    private void getNewSolicitorDetails(SecurityDTO securityDTO,
                                                           ChangeOrganisationRequest changeOrganisationRequest,
                                                       Map<String, Object> caseData, String id) {
        FindUsersByOrganisation organisationUser = findOrganisationDetails(securityDTO, changeOrganisationRequest, id);
        Optional<SolicitorUser> solicitorDetails = Optional.empty();
        if (null != organisationUser
                && null != organisationUser.getUsers()
                && !organisationUser.getUsers().isEmpty()) {
            solicitorDetails = organisationUser.getUsers()
                    .stream()
                    .filter(x -> changeOrganisationRequest.getCreatedBy().equalsIgnoreCase(
                            x.getEmail()))
                    .findFirst();
        }

        if (solicitorDetails.isPresent()) {
            SolicitorUser solicitorUser = solicitorDetails.get();
            caseData.put("solsSOTForenames", solicitorUser.getFirstName());
            caseData.put("solsSOTSurname", solicitorUser.getLastName());
            caseData.put("solsSolicitorEmail", solicitorUser.getEmail());

        } else {
            log.error(
                    "Notice of change: Solicitor {} does not belong to organisation id {}",
                    changeOrganisationRequest.getCreatedBy(),
                    changeOrganisationRequest.getOrganisationToAdd().getOrganisationID()
            );
        }
    }

    private FindUsersByOrganisation findOrganisationDetails(SecurityDTO securityDTO,
                                                            ChangeOrganisationRequest changeOrganisationRequest,
                                                            String caseId) {
        try {
            log.info("Get OrganisationUser for caseId {}", caseId);
            FindUsersByOrganisation organisationUser = organisationApi
                    .findSolicitorOrganisation(securityDTO.getAuthorisation(),
                    securityDTO.getServiceAuthorisation(),
                    changeOrganisationRequest.getOrganisationToAdd().getOrganisationID());
            log.info("Found OrganisationUser for caseId {}, OrganisationUser {}", caseId,
                    organisationUser);
            return organisationUser;
        } catch (Exception e) {
            log.error("Exception when looking up organisationUser for case {} for exception {}",
                    caseId, e.getMessage());
        }
        log.info("No OrganisationUser for caseId {}", caseId);
        return null;
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
        ChangeOrganisationRequest changeRequest = getChangeOrganisationRequest(caseData);
        return AddedRepresentative.builder()
                .organisationID(changeRequest.getOrganisationToAdd().getOrganisationID())
                .updatedBy(changeRequest.getCreatedBy())
                .updatedVia("NOC")
                .build();
    }

    public SolsAddress convertSolicitorAddress(OrganisationEntityResponse organisationResponse,
                                                Map<String, Object> caseData) {
        caseData.put("solsSolicitorFirmName", organisationResponse.getName());
        return objectMapper.convertValue(SolsAddress.builder()
                .addressLine1(organisationResponse.getContactInformation().get(0).getAddressLine1())
                .addressLine2(organisationResponse.getContactInformation().get(0).getAddressLine2())
                .addressLine3(organisationResponse.getContactInformation().get(0).getAddressLine3())
                .county(organisationResponse.getContactInformation().get(0).getCounty())
                .country(organisationResponse.getContactInformation().get(0).getCountry())
                .postTown(organisationResponse.getContactInformation().get(0).getTownCity())
                .postCode(organisationResponse.getContactInformation().get(0).getPostCode())
                .build(), SolsAddress.class);
    }

    private ChangeOrganisationRequest getChangeOrganisationRequest(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class);
    }

    private RemovedRepresentative getRemovedRepresentative(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("removedRepresentative"), RemovedRepresentative.class);
    }

    private List<CollectionMember<ChangeOfRepresentative>> getChangeOfRepresentations(Map<String, Object> caseData) {
        Object changeOfRepresentativesValue = caseData.get("changeOfRepresentatives");
        log.info("Change of reps value - " + changeOfRepresentativesValue);
        if (changeOfRepresentativesValue == null) {
            log.info("Change of reps - " + changeOfRepresentativesValue);
            return new ArrayList<>();
        }

        return objectMapper.convertValue(changeOfRepresentativesValue, List.class);
    }
}
