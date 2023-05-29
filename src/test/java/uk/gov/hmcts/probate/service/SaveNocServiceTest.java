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
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class SaveNocServiceTest {

    @InjectMocks
    private SaveNocService underTest;
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

        uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails details = new uk.gov.hmcts
                .probate.model.ccd.raw.request.CaseDetails(caseData, null, 0L);

        underTest.getRepresentatives(representatives);
    }
}
