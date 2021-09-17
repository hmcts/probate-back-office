package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeathRecordCCDServiceTest {

    DeathRecordCCDService deathRecordCCDService = new DeathRecordCCDService();

    @Test
    public void shouldMapDeathRecordToCCDFormat() {
        Deceased deceased = new Deceased();
        deceased.setForenames("Firstname");
        deceased.setSurname("LastName");
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        deceased.setAddress("An address");
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);
        v1Death.setId(1234);

        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord deathRecord =
            deathRecordCCDService.mapDeathRecord(v1Death);


        assertEquals(deathRecord.getSystemNumber(), v1Death.getId());
        assertEquals(deathRecord.getName(), String.format("%s %s", deceased.getForenames(), deceased.getSurname()));
        assertEquals(deathRecord.getDateOfBirth(), deceased.getDateOfBirth());
        assertEquals(deathRecord.getDateOfDeath(), deceased.getDateOfDeath());
        assertEquals(deathRecord.getSex(), deceased.getSex().getValue());
        assertEquals(deathRecord.getAddress(), deceased.getAddress());
    }
    
    @Test
    public void shouldHandleNull() {
        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord result =
            deathRecordCCDService.mapDeathRecord(null);

        assertNull(result);
    }


    @Test
    public void shouldHandleNullDeceased() {
        V1Death v1Death = new V1Death();
        v1Death.setId(1234);

        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord result =
            deathRecordCCDService.mapDeathRecord(v1Death);

        assertNotNull(null, result);
        assertEquals(1234, result.getSystemNumber());
    }
}