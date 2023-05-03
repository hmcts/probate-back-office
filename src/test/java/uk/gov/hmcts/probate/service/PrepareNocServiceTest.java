package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrepareNocServiceTest {

    @InjectMocks
    private PrepareNocService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddAndOrder() {
        List<CollectionMember<RemovedRepresentative>> removedRepresentatives = setupRemovedRepresentative();
        Organisation organisationData = Organisation.builder().organisationID("123")
                .organisationName("ABC").build();
        OrganisationPolicy policy = OrganisationPolicy.builder().organisation(organisationData).build();
        RemovedRepresentative removedRepresentative = RemovedRepresentative.builder()
                .organisationID(policy.getOrganisation().getOrganisationID())
                .organisationName(policy.getOrganisation().getOrganisationName())
                .solicitorEmail("abc@gmail.com")
                .solsAddress(SolsAddress.builder().postCode("SW1 0ZZ").build()).build();


        CaseData caseData = CaseData.builder()
                .removedRepresentatives(removedRepresentatives)
                .removedRepresentative(removedRepresentative)
                .applicantOrganisationPolicy(policy)
                .solsSOTForenames("First")
                .solsSOTSurname("Last")
                .build();

        underTest.addRemovedRepresentatives(caseData);

        assertEquals(3, caseData.getRemovedRepresentatives().size());
        assertEquals("First", caseData.getRemovedRepresentatives().get(0).getValue().getSolicitorFirstName());
        assertEquals("Last", caseData.getRemovedRepresentatives().get(0).getValue().getSolicitorLastName());
        assertNotNull(caseData.getRemovedRepresentatives().get(0).getValue().getAddedDateTime());
        assertEquals("First Name1",
                caseData.getRemovedRepresentatives().get(2).getValue().getSolicitorFirstName());
    }

    private List<CollectionMember<RemovedRepresentative>> setupRemovedRepresentative() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        List<CollectionMember<RemovedRepresentative>> removedRepresentatives = new ArrayList();
        Organisation organisationData = Organisation.builder().organisationID("123")
                .organisationName("ABC").build();
        OrganisationPolicy policy = OrganisationPolicy.builder().organisation(organisationData).build();
        CollectionMember<RemovedRepresentative> removedRepresentative1 =
                new CollectionMember<>(null, RemovedRepresentative
                .builder()
                .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                .organisationID(policy.getOrganisation().getOrganisationID())
                .organisationName(policy.getOrganisation().getOrganisationName())
                .solicitorFirstName("First Name1")
                .solicitorLastName("Last Name1")
                .solicitorEmail("abc@gmail.com")
                .build());
        CollectionMember<RemovedRepresentative> removedRepresentative2 =
                new CollectionMember<>(null, RemovedRepresentative
                .builder()
                .addedDateTime(LocalDateTime.parse("2023-01-01T18:00:00.001Z", dateTimeFormatter))
                .organisationID(policy.getOrganisation().getOrganisationID())
                .organisationName(policy.getOrganisation().getOrganisationName())
                .solicitorFirstName("First Name2")
                .solicitorLastName("Last Name2")
                .solicitorEmail("abc@gmail.com")
                .build());
        removedRepresentatives.add(removedRepresentative1);
        removedRepresentatives.add(removedRepresentative2);

        return removedRepresentatives;

    }
}
