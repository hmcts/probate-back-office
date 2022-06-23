package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.DeathRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeathRecordServiceTest {

    DeathRecordService deathRecordService = new DeathRecordService();

    @Test
    void mapDeathRecordsShouldHandleEmptyList() {
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService
                .mapDeathRecords(Collections.emptyList());
        assert (collectionMembers.isEmpty());
    }

    @Test
    void mapDeathRecordsShouldHandleNull() {
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService.mapDeathRecords(null);
        assert (collectionMembers.isEmpty());
    }

    @Test
    void mapDeathRecordsShouldHandleListWithNullElement() {
        ArrayList list = new ArrayList();
        list.add(null);
        final List<CollectionMember<DeathRecord>> collectionMembers = deathRecordService.mapDeathRecords(list);
        assert (collectionMembers.isEmpty());
    }

    @Test
    void shouldMapDeathRecord() {
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
    void shouldHandleDeceasedNull() {
        V1Death v1Death = new V1Death();
        final List<CollectionMember<DeathRecord>> deathRecordCollectionMembers = deathRecordService
                .mapDeathRecords(asList(v1Death));
        assertEquals(deathRecordCollectionMembers.size(), 1);
        final DeathRecord value = deathRecordCollectionMembers.get(0).getValue();

        assertEquals(value.getSystemNumber(), v1Death.getId());
    }

    @Test
    void shouldHandleNullNames() {

        Deceased deceased = new Deceased();
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);

        final List<CollectionMember<DeathRecord>> deathRecordCollectionMembers = deathRecordService
                .mapDeathRecords(asList(v1Death));
        assertEquals(deathRecordCollectionMembers.size(), 1);
        final DeathRecord value = deathRecordCollectionMembers.get(0).getValue();
    }
}
