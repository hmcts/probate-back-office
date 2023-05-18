package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.DecisionRequest;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddRepresentative() {
        List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = setupRepresentative();
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
                .build();

        CaseData caseData = CaseData.builder()
                .changeOfRepresentatives(changeOfRepresentatives)
                .changeOfRepresentative(representative)
                .removedRepresentative(removedRepresentative)
                .applicantOrganisationPolicy(policy)
                .solsSOTForenames("First")
                .solsSOTSurname("Last")
                .build();

        underTest.addRepresentatives(caseData);

        assertEquals(3, caseData.getChangeOfRepresentatives().size());
        /*assertEquals("First", caseData.getChangeOfRepresentatives().get(0)
                .getValue().getRemovedRepresentative().getSolicitorFirstName());
        assertEquals("Last", caseData.getChangeOfRepresentatives().get(0)
                .getValue().getRemovedRepresentative().getSolicitorLastName());*/
        assertEquals("NOC", caseData.getChangeOfRepresentatives().get(0)
                .getValue().getAddedRepresentative().getUpdatedVia());
        assertNotNull(caseData.getChangeOfRepresentatives().get(0).getValue().getAddedDateTime());
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
    public void testApplyDecision() {
        when(tokenGenerator.generate()).thenReturn("s2sToken");
        underTest.applyDecision(uk.gov.hmcts.reform.ccd.client.model.CallbackRequest.builder().build(), "testAuth");
        verify(assignCaseAccessClient, times(1))
                .applyDecision(Mockito.anyString(), Mockito.anyString(), Mockito.any(
                DecisionRequest.class));
    }

}
