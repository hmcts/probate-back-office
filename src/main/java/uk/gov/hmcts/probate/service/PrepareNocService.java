package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
        caseData.put("deceasedForenames","deceasedForenames123");
        caseDetails.getData().putAll(caseData);
        log.info("Deceased ForeName" + caseDetails.getData().get("deceasedForenames"));
        return assignCaseAccessClient.applyDecision(
                authorisation,
                tokenGenerator.generate(),
                decisionRequest(caseDetails)
        );
    }
}
