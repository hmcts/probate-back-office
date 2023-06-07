package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.reform.probate.model.cases.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.Organisation;
import uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative;
import uk.gov.hmcts.reform.probate.model.cases.RemovedRepresentative;
import uk.gov.hmcts.reform.probate.model.cases.AddedRepresentative;
import uk.gov.hmcts.reform.probate.model.cases.OrganisationPolicy;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaveNocService {

    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public List<CollectionMember<ChangeOfRepresentative>> getRepresentatives(
            List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember
                    <uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative>> reps) {
        List<CollectionMember<ChangeOfRepresentative>> representatives =
                new ArrayList<>();
        for (uk.gov.hmcts.probate.model.ccd.raw.CollectionMember
                <uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative> repCollectionMember :
                reps) {
            representatives.add(new CollectionMember<>(
                    repCollectionMember.getId(),
                    getRepresentative(repCollectionMember.getValue())));
        }
        return representatives;
    }

    private ChangeOfRepresentative getRepresentative(uk.gov.hmcts.probate.model.ccd.raw
                                                              .ChangeOfRepresentative representative) {
        AddedRepresentative added = AddedRepresentative.builder()
                .organisationID(representative.getAddedRepresentative().getOrganisationID())
                .updatedBy(representative.getAddedRepresentative().getUpdatedBy())
                .updatedVia(representative.getAddedRepresentative().getUpdatedVia())
                .build();

        Organisation organisation = Organisation.builder()
                .organisationID(representative.getRemovedRepresentative().getOrganisation().getOrganisationID())
                .organisationName(representative.getRemovedRepresentative().getOrganisation().getOrganisationName())
                .build();

        RemovedRepresentative removed = RemovedRepresentative.builder()
                        .organisationID(representative.getRemovedRepresentative().getOrganisationID())
                        .solicitorFirstName(representative.getRemovedRepresentative().getSolicitorFirstName())
                        .solicitorLastName(representative.getRemovedRepresentative().getSolicitorLastName())
                        .solicitorEmail(representative.getRemovedRepresentative().getSolicitorEmail())
                        .organisation(organisation)
                        .build();
        return ChangeOfRepresentative.builder()
                .addedDateTime(representative.getAddedDateTime())
                .addedRepresentative(added)
                .removedRepresentative(removed)
                .build();

    }

    public void addRepresentatives(CallbackRequest callbackRequest) {
        CaseDetails caseDetailsBefore = callbackRequest.getCaseDetailsBefore();
        Map<String, Object> oldCaseData = caseDetailsBefore.getData();
        log.info("Old case data - " + oldCaseData);
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Map<String, Object> caseData = caseDetails.getData();
        log.info("New case data - " + caseData);
        log.info("change organisation request- " + oldCaseData.get("changeOrganisationRequestField"));
        ChangeOrganisationRequest changeRequest = getChangeOrganisationRequest(caseDetailsBefore);
        log.info("change organisation request after- " + changeRequest);
        List<CollectionMember<ChangeOfRepresentative>> representatives = getChangeOfRepresentations(caseData);
        log.info("reps before- " + representatives);
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
                .changeOfRepresentatives(representatives)
                .build();
        log.info("Grant of representation data - " + grantOfRepresentationData);
        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseDetails.getId().toString(),
                grantOfRepresentationData, EventId.APPLY_DECISION,
                securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), "Apply Noc",
                "Apply Noc");
    }

    private ChangeOrganisationRequest getChangeOrganisationRequest(CaseDetails caseDetails) {

        return objectMapper.convertValue(caseDetails.getData().get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class);
    }

    private List<CollectionMember<ChangeOfRepresentative>> getChangeOfRepresentations(Map<String, Object> caseData) {
        Object changeOfRepresentativesValue = caseData.get("changeOfRepresentatives");
        if (changeOfRepresentativesValue == null) {
            log.info("Change of reps - " + changeOfRepresentativesValue);
            return new ArrayList<>();
        }
        return objectMapper.convertValue(caseData.get("changeOfRepresentatives"), List.class);
    }

    private RemovedRepresentative getRemovedRepresentative(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("removedRepresentative"),
                RemovedRepresentative.class);
    }

    public OrganisationPolicy getOrganisationPolicy(Map<String, Object> caseData) {

        return objectMapper.convertValue(caseData.get("applicantOrganisationPolicy"),
                OrganisationPolicy.class);
    }

    private ChangeOfRepresentative buildRepresentative(Map<String, Object> caseData) {
        RemovedRepresentative removeRepresentative = getRemovedRepresentative(caseData);
        AddedRepresentative addRepresentative = setAddedRepresentative(caseData);
        log.info("Removed Representative - " + removeRepresentative);
        log.info("Added Representative - " + addRepresentative);
        return ChangeOfRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .addedRepresentative(addRepresentative)
                .removedRepresentative(removeRepresentative)
                .build();
    }

    private AddedRepresentative setAddedRepresentative(Map<String, Object>  caseData) {
        OrganisationPolicy organisationPolicy = getOrganisationPolicy(caseData);
        Organisation organisation = organisationPolicy.getOrganisation();
        return AddedRepresentative.builder()
                .organisationID(organisation.getOrganisationID())
                .updatedBy("changeOrganisationRequest.getCreatedBy()")
                .updatedVia("NOC")
                .build();
    }
}
