package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.DecisionRequest;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.caseaccess.SolicitorUser;
import uk.gov.hmcts.probate.model.caseaccess.FindUsersByOrganisation;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.payments.pba.ContactInformationResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.OrganisationApi;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrepareNocServiceTest {

    @InjectMocks
    private PrepareNocService underTest;
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
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private OrganisationApi organisationApi;
    @Mock
    private  OrganisationsRetrievalService organisationsRetrievalService;
    private Map<String, Object> caseData;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        OrganisationPolicy organisationPolicy = OrganisationPolicy.builder()
                .organisation(Organisation.builder()
                        .organisationID("orgId1")
                        .organisationName("OrgName1").build()).build();
        ChangeOrganisationRequest changeRequest = ChangeOrganisationRequest.builder()
                .createdBy("abc@gmail.com")
                .organisationToAdd(Organisation.builder().organisationID("12").build()).build();
        RemovedRepresentative removed = RemovedRepresentative.builder()
                .organisationID(organisationPolicy.getOrganisation().getOrganisationID())
                .organisation(organisationPolicy.getOrganisation())
                .solicitorEmail("abc@gmail.com")
                .solicitorFirstName("First")
                .solicitorLastName("Last")
                .build();

        caseData = new HashMap<>();
        caseData.put("removedRepresentative", removed);
        caseData.put("changeOrganisationRequestField", changeRequest);
        caseData.put("applicantOrganisationPolicy",organisationPolicy);
        List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = setupRepresentative();
        caseData.put("changeOfRepresentatives",changeOfRepresentatives);
        SolsAddress address = SolsAddress.builder().addressLine1("Address Line1").addressLine2("Line2")
                .country("United Kingdom").postCode("sw2").build();
        caseData.put("solsSolicitorAddress",address);

        when(objectMapper.convertValue(caseData.get("applicantOrganisationPolicy"),
                OrganisationPolicy.class)).thenReturn(organisationPolicy);
        when(objectMapper.convertValue(caseData.get("removedRepresentative"),
                RemovedRepresentative.class)).thenReturn(removed);
        when(objectMapper.convertValue(caseData.get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class)).thenReturn(changeRequest);
        when(objectMapper.convertValue(caseData.get("changeOfRepresentatives"),
                List.class)).thenReturn(changeOfRepresentatives);

        when(tokenGenerator.generate()).thenReturn("s2sToken");

        OrganisationEntityResponse organisationEntityResponse = OrganisationEntityResponse.builder()
                .contactInformation(Arrays.asList(ContactInformationResponse.builder().addressLine1("Line1")
                        .addressLine1("Line2").addressLine3("Line3")
                        .country("UK").townCity("city").postcode("abc").build())).build();
        when(organisationApi.findOrganisationByOrgId(anyString(), anyString(), anyString()))
                .thenReturn(organisationEntityResponse);
        when(objectMapper.convertValue(organisationEntityResponse,
                SolsAddress.class)).thenReturn(address);
        FindUsersByOrganisation organisationUser = FindUsersByOrganisation.builder()
                .users(Arrays.asList(SolicitorUser.builder()
                        .firstName("first").lastName("last").email("abc@gmail.com").build())).build();

        when(organisationApi.findSolicitorOrganisation(anyString(), anyString(), anyString()))
                .thenReturn(organisationUser);
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();

        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
    }

    @Test
    void removeRepresentative() {
        Organisation organisationData = Organisation.builder().organisationID("123")
                .organisationName("ABC").build();
        OrganisationPolicy policy = OrganisationPolicy.builder().organisation(organisationData).build();
        CaseData caseData = CaseData.builder()
                .applicantOrganisationPolicy(policy)
                .solsSOTForenames("First")
                .solsSOTSurname("Last")
                .build();
        underTest.setRemovedRepresentative(caseData);
        assertEquals("First", caseData.getRemovedRepresentative().getSolicitorFirstName());

    }

    @Test
     void testApplyDecision() {
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).build())
                .build();
        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(
                DecisionRequest.class));
    }

    @Test
     void testWhenFindOrganisationThrowsException() {
        doThrow(new NullPointerException()).when(organisationApi)
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).build())
                .build();
        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(
                        DecisionRequest.class));
    }

    @Test
    void testWhenFindByOrgIdThrowsException() {
        doThrow(new NullPointerException()).when(organisationApi)
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).build())
                .build();
        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(
                        DecisionRequest.class));
    }

    private List<CollectionMember<ChangeOfRepresentative>> setupRepresentative() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        List<CollectionMember<ChangeOfRepresentative>> representatives = new ArrayList();
        CollectionMember<ChangeOfRepresentative> removedRepresentative1 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                        .build());
        CollectionMember<ChangeOfRepresentative> removedRepresentative2 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2023-01-01T18:00:00.001Z", dateTimeFormatter))
                        .build());
        representatives.add(removedRepresentative1);
        representatives.add(removedRepresentative2);

        return representatives;
    }

}
