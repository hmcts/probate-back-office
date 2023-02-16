package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrarDirectionServiceTest {

    @InjectMocks
    private RegistrarDirectionService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleRedeclarationNotificationShouldBeSuccessful() {
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
        RegistrarDirection registrarDirectionToAdd = RegistrarDirection
                .builder()
                .addedDateTime(LocalDateTime.now())
                .decision("otherOrder")
                .furtherInformation("futher info 4")
                .build();
        registrarDirections.add(registrarDirection1);
        registrarDirections.add(registrarDirection2);
        registrarDirections.add(registrarDirection3);

        CaseData caseData = CaseData.builder()
                .registrarDirections(registrarDirections)
                .registrarDirectionToAdd(registrarDirectionToAdd)
                .build();

        underTest.addAndOrderDirections(caseData);

        assertEquals(4, caseData.getRegistrarDirections().size());
        assertEquals(registrarDirectionToAdd, caseData.getRegistrarDirections().get(0).getValue());
        assertEquals(registrarDirection2, caseData.getRegistrarDirections().get(1));
        assertEquals(registrarDirection1, caseData.getRegistrarDirections().get(2));
        assertEquals(registrarDirection3, caseData.getRegistrarDirections().get(3));
    }
}
