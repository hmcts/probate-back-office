package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegistrarDirectionServiceTest {

    @InjectMocks
    private RegistrarDirectionService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddAndOrderDirectionsToGrant() {
        List<CollectionMember<RegistrarDirection>> registrarDirections = setupRegistrarDirections();
        RegistrarDirection registrarDirectionToAdd = RegistrarDirection
                .builder()
                .decision("otherOrder")
                .furtherInformation("futher info 4")
                .build();

        CaseData caseData = CaseData.builder()
                .registrarDirections(registrarDirections)
                .registrarDirectionToAdd(registrarDirectionToAdd)
                .build();

        underTest.addAndOrderDirectionsToGrant(caseData);

        assertEquals(4, caseData.getRegistrarDirections().size());
        assertEquals(registrarDirectionToAdd.getDecision(), caseData.getRegistrarDirections().get(0).getValue()
                .getDecision());
        assertEquals(registrarDirectionToAdd.getFurtherInformation(), caseData.getRegistrarDirections().get(0)
                .getValue()
                .getFurtherInformation());
        assertNotNull(caseData.getRegistrarDirections().get(0).getValue().getAddedDateTime());
        assertEquals("lostWill", caseData.getRegistrarDirections().get(1).getValue().getDecision());
        assertEquals("probateRefused", caseData.getRegistrarDirections().get(2).getValue().getDecision());
        assertEquals("otherOrder", caseData.getRegistrarDirections().get(3).getValue().getDecision());
        assertEquals("Yes", caseData.getEvidenceHandled());
    }

    @Test
    void shouldAddAndOrderDirectionsToCaveat() {
        List<CollectionMember<RegistrarDirection>> registrarDirections = setupRegistrarDirections();
        RegistrarDirection registrarDirectionToAdd = RegistrarDirection
                .builder()
                .decision("otherOrder")
                .furtherInformation("futher info 4")
                .build();

        CaveatData caseData = CaveatData.builder()
                .registrarDirections(registrarDirections)
                .registrarDirectionToAdd(registrarDirectionToAdd)
                .build();

        underTest.addAndOrderDirectionsToCaveat(caseData);

        assertEquals(4, caseData.getRegistrarDirections().size());
        assertEquals(registrarDirectionToAdd.getDecision(), caseData.getRegistrarDirections().get(0).getValue()
                .getDecision());
        assertEquals(registrarDirectionToAdd.getFurtherInformation(), caseData.getRegistrarDirections().get(0)
                .getValue()
                .getFurtherInformation());
        assertNotNull(caseData.getRegistrarDirections().get(0).getValue().getAddedDateTime());
        assertEquals("lostWill", caseData.getRegistrarDirections().get(1).getValue().getDecision());
        assertEquals("probateRefused", caseData.getRegistrarDirections().get(2).getValue().getDecision());
        assertEquals("otherOrder", caseData.getRegistrarDirections().get(3).getValue().getDecision());
    }

    private List<CollectionMember<RegistrarDirection>> setupRegistrarDirections() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        List<CollectionMember<RegistrarDirection>> registrarDirections = new ArrayList();
        CollectionMember<RegistrarDirection> registrarDirection1 = new CollectionMember<>(null, RegistrarDirection
                .builder()
                .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                .decision("probateRefused")
                .furtherInformation("futher info 1")
                .build());
        CollectionMember<RegistrarDirection> registrarDirection2 = new CollectionMember<>(null, RegistrarDirection
                .builder()
                .addedDateTime(LocalDateTime.parse("2023-01-01T18:00:00.001Z", dateTimeFormatter))
                .decision("lostWill")
                .build());
        CollectionMember<RegistrarDirection> registrarDirection3 = new CollectionMember<>(null, RegistrarDirection
                .builder()
                .addedDateTime(LocalDateTime.parse("2022-02-02T10:00:00.001Z", dateTimeFormatter))
                .decision("otherOrder")
                .furtherInformation("futher info 3")
                .build());
        registrarDirections.add(registrarDirection1);
        registrarDirections.add(registrarDirection2);
        registrarDirections.add(registrarDirection3);

        return registrarDirections;

    }
}
