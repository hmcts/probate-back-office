package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.DecisionRequest;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.SolicitorUser;
import uk.gov.hmcts.probate.model.caseaccess.FindUsersByOrganisation;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.payments.pba.ContactInformationResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.OrganisationApi;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_BULKSCAN;

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
    private SecurityUtils securityUtils;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private OrganisationApi organisationApi;
    private Map<String, Object> caseData;
    private OrganisationEntityResponse organisationEntityResponse;
    private ContactInformationResponse contactInformationResponse;
    private SecurityDTO securityDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ChangeOrganisationRequest changeRequest = ChangeOrganisationRequest.builder()
                .createdBy("sol2@gmail.com")
                .organisationToAdd(Organisation.builder().organisationID("12").build())
                .organisationToRemove(Organisation.builder().organisationID("13")
                .organisationName("OldOrg").build()).build();

        caseData = new HashMap<>();
        caseData.put("changeOrganisationRequestField", changeRequest);
        List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = setupRepresentative();
        caseData.put("changeOfRepresentatives",changeOfRepresentatives);
        SolsAddress address = SolsAddress.builder().addressLine1("Address Line1").addressLine2("Line2")
                .country("United Kingdom").postCode("sw2").county("county").build();
        caseData.put("solsSolicitorAddress",address);
        caseData.put("solsSOTForenames","OldSolicitorFirstName");
        caseData.put("solsSOTSurname","OldSolicitorLastName");
        caseData.put("solsSolicitorEmail","OldSolicitor@gmail.com");

        when(objectMapper.convertValue(caseData.get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class)).thenReturn(changeRequest);
        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(changeOfRepresentatives);
        when(objectMapper.convertValue(any(), eq(SolsAddress.class))).thenReturn(address);

        when(tokenGenerator.generate()).thenReturn("s2sToken");

        contactInformationResponse = ContactInformationResponse.builder().addressLine1("Line1")
                .addressLine2("Line2").addressLine3("Line3")
                .country("UK").townCity("city").postCode("sw2").county("county").build();

        organisationEntityResponse = OrganisationEntityResponse.builder().name("Org2 name")
                .contactInformation(Arrays.asList(contactInformationResponse)).build();
        when(organisationApi.findOrganisationByOrgId(anyString(), anyString(), anyString()))
                .thenReturn(organisationEntityResponse);
        when(objectMapper.convertValue(organisationEntityResponse,
                SolsAddress.class)).thenReturn(address);
        FindUsersByOrganisation organisationUser = FindUsersByOrganisation.builder()
                .users(Arrays.asList(SolicitorUser.builder()
                        .firstName("Sol2First").lastName("Sol2LastName").email("sol2@gmail.com").build())).build();

        when(organisationApi.findSolicitorOrganisation(anyString(), anyString(), anyString()))
                .thenReturn(organisationUser);
        securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();

        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
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
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
    void testApplyDecisionForBulkScanCases() {
        caseData.put("changeOfRepresentatives",null);
        caseData.put("channelChoice",CHANNEL_CHOICE_BULKSCAN);
        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(null);
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).caseTypeId("GrantOfRepresentation").id(0L).build())
                .build();

        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
    void testApplyDecisionForCaveatBulkScanCases() {
        caseData.put("changeOfRepresentatives",null);
        caseData.put("paperForm","Yes");
        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(null);
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).caseTypeId("Caveat").id(0L).build())
                .build();

        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
    void testApplyDecisionForCaveatDigitalCases() {
        caseData.put("paperForm","Yes");
        caseData.put("caveatorEmailAddress", "testEmail@gmail.com");
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).caseTypeId("Caveat").id(0L).build())
                .build();

        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
    void testForNullRepresentativesBefore() {
        caseData.put("changeOfRepresentatives",null);
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).build())
                .build();
        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
    void testForEmptyListOfRepresentativesBefore() {
        List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = new ArrayList<>();
        caseData.put("changeOfRepresentatives",changeOfRepresentatives);
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).build())
                .build();
        underTest.applyDecision(request, "testAuth");
        verify(organisationApi, times(1))
                .findOrganisationByOrgId(anyString(), anyString(), anyString());
        verify(organisationApi, times(1))
                .findSolicitorOrganisation(anyString(), anyString(), anyString());
        verify(assignCaseAccessClient, times(1))
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
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
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
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
                .applyDecision(anyString(), anyString(), any(DecisionRequest.class));
    }

    @Test
     void shouldConvertOrganisationAddress() {

        when(organisationApi.findOrganisationByOrgId("auth", "authToken",
                "organisationId")).thenReturn(organisationEntityResponse);
        SolsAddress solAddress = SolsAddress.builder().addressLine1("Line1").addressLine2("Line2")
                .country("UK").postCode("sw2").county("county").postTown("city").build();
        when(objectMapper.convertValue(any(), eq(SolsAddress.class))).thenReturn(solAddress);

        SolsAddress address = underTest.getNewSolicitorAddress(securityDTO, "organisationId",
                caseData, "1L");


        assertEquals(address.getAddressLine1(),
                organisationEntityResponse.getContactInformation().getFirst().getAddressLine1());
        assertEquals(address.getCounty(),
                organisationEntityResponse.getContactInformation().getFirst().getCounty());
        assertEquals(address.getCountry(),
                organisationEntityResponse.getContactInformation().getFirst().getCountry());
        assertEquals(address.getPostTown(),
                organisationEntityResponse.getContactInformation().getFirst().getTownCity());
        assertEquals(address.getPostCode(),
                organisationEntityResponse.getContactInformation().getFirst().getPostCode());

    }

    private List<CollectionMember<ChangeOfRepresentative>> setupRepresentative() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        List<CollectionMember<ChangeOfRepresentative>> representatives = new ArrayList<>();
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
