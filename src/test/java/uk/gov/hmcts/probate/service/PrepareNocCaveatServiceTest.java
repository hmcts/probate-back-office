package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.DecisionRequest;
import uk.gov.hmcts.probate.model.caseaccess.FindUsersByOrganisation;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.SolicitorUser;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
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
import java.util.Collections;
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

class PrepareNocCaveatServiceTest {

    @InjectMocks
    private PrepareNocCaveatService underTest;
    @Mock
    AssignCaseAccessClient assignCaseAccessClient;
    @Mock
    AuthTokenGenerator tokenGenerator;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private OrganisationApi organisationApi;
    @Mock
    private PrepareNocService prepareNocServiceMock;
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
        RemovedRepresentative removed = RemovedRepresentative.builder()
                .organisationID(changeRequest.getOrganisationToRemove().getOrganisationID())
                .organisation(changeRequest.getOrganisationToRemove())
                .solicitorEmail("OldSolicitor@gmail.com")
                .build();
        caseData = new HashMap<>();
        caseData.put("removedRepresentative", removed);
        caseData.put("changeOrganisationRequestField", changeRequest);
        caseData.put("caveatorEmailAddress","OldSolicitor@gmail.com");
        List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = setupRepresentative();
        caseData.put("changeOfRepresentatives",changeOfRepresentatives);
        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address Line1").proAddressLine2("Line2")
                .proCountry("United Kingdom").proPostCode("sw2").proCounty("county").build();
        caseData.put("caveatorAddress",address);
        securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        ChangeOfRepresentative changeOfRepresentative  = ChangeOfRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .addedRepresentative(AddedRepresentative.builder().organisationID("12")
                        .updatedBy("abc.gmail.com").build())
                .removedRepresentative(removed).build();
        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        SolicitorUser solicitorUser = SolicitorUser.builder()
                .firstName("Sol2First").lastName("Sol2LastName").email("sol2@gmail.com").build();
        FindUsersByOrganisation organisationUser = FindUsersByOrganisation.builder()
                .users(Collections.singletonList(solicitorUser)).build();
        when(organisationApi.findSolicitorOrganisation(anyString(), anyString(), anyString()))
                .thenReturn(organisationUser);
        when(prepareNocServiceMock.buildChangeOfRepresentative(any(), any(), any(), any(), any()))
                .thenReturn(changeOfRepresentative);
        when(objectMapper.convertValue(caseData.get("removedRepresentative"),
                RemovedRepresentative.class)).thenReturn(removed);
        when(objectMapper.convertValue(caseData.get("changeOrganisationRequestField"),
                ChangeOrganisationRequest.class)).thenReturn(changeRequest);
        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(changeOfRepresentatives);
        when(objectMapper.convertValue(any(), eq(ProbateAddress.class))).thenReturn(address);

        when(tokenGenerator.generate()).thenReturn("s2sToken");

        contactInformationResponse = ContactInformationResponse.builder().addressLine1("Line1")
                .addressLine2("Line2").addressLine3("Line3")
                .country("UK").townCity("city").postCode("sw2").county("county").build();

        organisationEntityResponse = OrganisationEntityResponse.builder().name("Org2 name")
                .contactInformation(Arrays.asList(contactInformationResponse)).build();
        when(organisationApi.findOrganisationByOrgId(anyString(), anyString(), anyString()))
                .thenReturn(organisationEntityResponse);
    }

    @Test
     void testApplyDecision() {
        CallbackRequest request = CallbackRequest.builder()
                .caseDetails(CaseDetails.builder().data(caseData).id(0L).caseTypeId("Caveat").build())
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
                .applyDecision(anyString(), anyString(), any(
                        DecisionRequest.class));
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

    @Test
     void shouldConvertOrganisationAddress() {

        when(organisationApi.findOrganisationByOrgId("auth", "authToken",
                "organisationId")).thenReturn(organisationEntityResponse);
        ProbateAddress solAddress = ProbateAddress.builder().proAddressLine1("Line1").proAddressLine2("Line2")
                .proCountry("UK").proPostCode("sw2").proCounty("county").proPostTown("city").build();
        when(objectMapper.convertValue(any(), eq(ProbateAddress.class))).thenReturn(solAddress);

        ProbateAddress address = underTest.getNewSolicitorAddress(securityDTO, "organisationId",
                caseData, "1L");


        assertEquals(address.getProAddressLine1(),
                organisationEntityResponse.getContactInformation().get(0).getAddressLine1());
        assertEquals(address.getProCounty(),
                organisationEntityResponse.getContactInformation().get(0).getCounty());
        assertEquals(address.getProCountry(),
                organisationEntityResponse.getContactInformation().get(0).getCountry());
        assertEquals(address.getProPostTown(),
                organisationEntityResponse.getContactInformation().get(0).getTownCity());
        assertEquals(address.getProPostCode(),
                organisationEntityResponse.getContactInformation().get(0).getPostCode());

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
