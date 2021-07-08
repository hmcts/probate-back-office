package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import org.junit.Test;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.DeathRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeathRecordServiceTest {

    DeathRecordService deathRecordService = new DeathRecordService();

    @Test
    public void mapDeathRecordsShouldHandleEmptyList() {
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService
                .mapDeathRecords(Collections.emptyList());
        assert (collectionMembers.isEmpty());
    }

    @Test
    public void mapDeathRecordsShouldHandleNull() {
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService.mapDeathRecords(null);
        assert (collectionMembers.isEmpty());
    }


    @Test
    public void mapDeathRecordsShouldHandleListWilthNullElement() {
        ArrayList list = new ArrayList();
        list.add(null);
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService.mapDeathRecords(list);
        assert (collectionMembers.isEmpty());
    }

    @Test
    public void shouldMapDeathRecord() {
        Deceased deceased = new Deceased();
        deceased.setForenames("Firstname");
        deceased.setSurname("LastName");
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        deceased.setAddress("An address");
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);
        v1Death.setId(1234);

        final List<CollectionMember<DeathRecord>> deathRecordCollectionMembers = deathRecordService
                .mapDeathRecords(asList(v1Death));
        assertEquals(deathRecordCollectionMembers.size(), 1);
        final DeathRecord value = deathRecordCollectionMembers.get(0).getValue();

        assertEquals(value.getSystemNumber(), v1Death.getId());
        assertEquals(value.getName(), String.format("%s %s", deceased.getForenames(), deceased.getSurname()));
        assertEquals(value.getDateOfBirth(), deceased.getDateOfBirth());
        assertEquals(value.getDateOfDeath(), deceased.getDateOfDeath());
        assertEquals(value.getSex(), deceased.getSex().getValue());
        assertEquals(value.getAddress(), deceased.getAddress());
    }

    @Test
    public void shouldHandleDeceasedNull() {
        V1Death v1Death = new V1Death();
        final List<CollectionMember<DeathRecord>> deathRecordCollectionMembers = deathRecordService
                .mapDeathRecords(asList(v1Death));
        assertEquals(deathRecordCollectionMembers.size(), 1);
        final DeathRecord value = deathRecordCollectionMembers.get(0).getValue();

        assertEquals(value.getSystemNumber(), v1Death.getId());
    }

    @Test
    public void shouldHandleNullNames() {

        Deceased deceased = new Deceased();
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);

        final List<CollectionMember<DeathRecord>> deathRecordCollectionMembers = deathRecordService
                .mapDeathRecords(asList(v1Death));
        assertEquals(deathRecordCollectionMembers.size(), 1);
        final DeathRecord value = deathRecordCollectionMembers.get(0).getValue();
    }
    
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
            deathRecordService.mapDeathRecordCCD(v1Death);


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
            deathRecordService.mapDeathRecordCCD(null);

        assertNull(result);
    }


    @Test
    public void shouldHandleNullDeceased() {
        V1Death v1Death = new V1Death();
        v1Death.setId(1234);

        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord result =
            deathRecordService.mapDeathRecordCCD(v1Death);

        assertNotNull(null, result);
        assertEquals(1234, result.getSystemNumber());
    }
}