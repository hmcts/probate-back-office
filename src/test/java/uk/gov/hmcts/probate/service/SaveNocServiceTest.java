package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaveNocServiceTest {

    @InjectMocks
    private SaveNocService underTest;
    @Mock
    AssignCaseAccessClient assignCaseAccessClient;
    @Mock
    AuthTokenGenerator tokenGenerator;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CcdClientApi ccdClientApi;
    @Mock
    private SecurityUtils securityUtils;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddRepresentative() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Organisation organisationData = Organisation.builder().organisationID("123")
                .organisationName("ABC").build();
        OrganisationPolicy policy = OrganisationPolicy.builder().organisation(organisationData).build();
        RemovedRepresentative removedRepresentative = RemovedRepresentative.builder()
                .organisationID(policy.getOrganisation().getOrganisationID())
                .organisation(policy.getOrganisation())
                .solicitorEmail("abc@gmail.com")
                .solicitorFirstName("First")
                .solicitorLastName("Last")
                .build();
        AddedRepresentative addedRepresentative = AddedRepresentative.builder()
                .organisationID(policy.getOrganisation().getOrganisationID())
                .updatedVia("abc").build();
        ChangeOfRepresentative representative = ChangeOfRepresentative.builder()
                .addedRepresentative(addedRepresentative)
                .removedRepresentative(removedRepresentative)
                .build();
        List<CollectionMember<ChangeOfRepresentative>> representatives = new ArrayList();
        CollectionMember<ChangeOfRepresentative> representative1 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                        .removedRepresentative(removedRepresentative)
                        .addedRepresentative(addedRepresentative)
                        .build());
        representatives.add(representative1);

        CaseData caseData = CaseData.builder()
                .changeOfRepresentatives(representatives)
                .changeOfRepresentative(representative)
                .removedRepresentative(removedRepresentative)
                .applicantOrganisationPolicy(policy)
                .solsSOTForenames("First")
                .solsSOTSurname("Last")
                .build();

        underTest.getRepresentatives(representatives);
    }

    @Test
    void addRepresentatives() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        uk.gov.hmcts.reform.probate.model.cases.OrganisationPolicy organisationPolicy =
                uk.gov.hmcts.reform.probate.model.cases.OrganisationPolicy.builder()
                        .organisation(uk.gov.hmcts.reform.probate.model.cases.Organisation.builder()
                                .organisationID("orgId1")
                                .organisationName("OrgName1").build()).build();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("CreatedBy","abc@gmail.com");
        uk.gov.hmcts.reform.probate.model.cases.RemovedRepresentative removed =
                uk.gov.hmcts.reform.probate.model.cases.RemovedRepresentative.builder()
                        .organisationID(organisationPolicy.getOrganisation().getOrganisationID())
                        .organisation(organisationPolicy.getOrganisation())
                        .solicitorEmail("abc@gmail.com")
                        .solicitorFirstName("First")
                        .solicitorLastName("Last")
                        .build();
        uk.gov.hmcts.reform.probate.model.cases.AddedRepresentative added =
                uk.gov.hmcts.reform.probate.model.cases.AddedRepresentative.builder()
                        .organisationID("orgId2")
                        .updatedBy("changeOrganisationRequest.getCreatedBy()")
                        .updatedVia("NOC")
                        .build();
        uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative representative =
                uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative.builder()
                        .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                        .addedRepresentative(added)
                        .removedRepresentative(removed)
                        .build();
        List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember
                <uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative>> representatives
                = new ArrayList<>();
        uk.gov.hmcts.reform.probate.model.cases.CollectionMember
                <uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative> representative1 =
                new uk.gov.hmcts.reform.probate.model.cases.CollectionMember<>(null, representative);
        representatives.add(representative1);

        Map<String, Object> caseData = new HashMap<>();
        caseData.put("removedRepresentative", removed);
        caseData.put("changeOrganisationRequestField", map);
        caseData.put("applicantOrganisationPolicy",organisationPolicy);
        caseData.put("changeOfRepresentatives",representatives);

        CaseDetails caseDetails = CaseDetails.builder().data(caseData)
                .id(0L).build();
        CallbackRequest callbackRequest = CallbackRequest.builder().caseDetails(caseDetails)
                .caseDetailsBefore(caseDetails).build();
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();

        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        underTest.addRepresentatives(callbackRequest);
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(),
                        any(), any(), any(), any());
    }
}
