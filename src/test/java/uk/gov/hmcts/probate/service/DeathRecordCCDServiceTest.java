package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Alias;
import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeathRecordCCDServiceTest {

    DeathRecordCCDService deathRecordCCDService = new DeathRecordCCDService();

    @Test
    void shouldMapDeathRecordToCCDFormat() {
        Alias alias = new Alias();
        alias.setForenames("AliasFirstName");
        alias.setSurname("AliasLastName");
        alias.setType("AliasType");
        alias.setPrefix("AliasPrefix");
        alias.setSuffix("AliasSuffix");
        Deceased deceased = new Deceased();
        deceased.setForenames("Firstname");
        deceased.setSurname("LastName");
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        deceased.setAddress("An address");
        deceased.setAliases(List.of(alias));
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);
        v1Death.setId(1234);

        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord deathRecord =
            deathRecordCCDService.mapDeathRecord(v1Death);

        uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Alias mappedAlias =
                deathRecord.getAliases().getFirst().getValue();
        assertAll(
                () -> assertEquals(v1Death.getId(), deathRecord.getSystemNumber()),
                () -> assertEquals(
                        String.format("%s %s", deceased.getForenames(), deceased.getSurname()),
                        deathRecord.getName()
                ),
                () -> assertEquals(deceased.getDateOfBirth(), deathRecord.getDateOfBirth()),
                () -> assertEquals(deceased.getDateOfDeath(), deathRecord.getDateOfDeath()),
                () -> assertEquals(deceased.getSex().getValue(), deathRecord.getSex()),
                () -> assertEquals(deceased.getAddress(), deathRecord.getAddress()),

                () -> assertEquals(alias.getForenames(), mappedAlias.getForenames()),
                () -> assertEquals(alias.getSurname(), mappedAlias.getLastName()),
                () -> assertEquals(alias.getType(), mappedAlias.getType()),
                () -> assertEquals(alias.getPrefix(), mappedAlias.getPrefix()),
                () -> assertEquals(alias.getSuffix(), mappedAlias.getSuffix())
        );
    }

    @Test
    void shouldHandleNull() {
        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord result =
            deathRecordCCDService.mapDeathRecord(null);

        assertNull(result);
    }


    @Test
    void shouldHandleNullDeceased() {
        V1Death v1Death = new V1Death();
        v1Death.setId(1234);

        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord result =
            deathRecordCCDService.mapDeathRecord(v1Death);

        assertNotNull(result);
        assertEquals(1234, result.getSystemNumber());
    }


    @Test
    void mapDeathRecordsCCDShouldHandleEmptyList() {
        final List<CollectionMember<DeathRecord>>
            collectionMembers = deathRecordCCDService
            .mapDeathRecords(Collections.emptyList());
        assert (collectionMembers.isEmpty());
    }

    @Test
    void mapDeathRecordsCCDShouldHandleNull() {
        final List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.DeathRecord>>
            collectionMembers = deathRecordCCDService.mapDeathRecords(null);
        assert (collectionMembers.isEmpty());
    }


    @Test
    void mapDeathRecordsCCDShouldHandleListWithNullElement() {
        ArrayList list = new ArrayList();
        list.add(null);
        final List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.DeathRecord>>
            collectionMembers = deathRecordCCDService.mapDeathRecords(list);
        assert (collectionMembers.isEmpty());
    }

    @Test
    void shouldMapDeathRecordCCD() {
        Deceased deceased = new Deceased();
        deceased.setForenames("Firstname");
        deceased.setSurname("LastName");
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        deceased.setAddress("An address");
        V1Death v1Death = new V1Death();
        v1Death.setDeceased(deceased);
        v1Death.setId(1234);

        final List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.DeathRecord>>
            deathRecordCollectionMembers = deathRecordCCDService.mapDeathRecords(asList(v1Death));

        assertEquals(deathRecordCollectionMembers.size(), 1);
        final uk.gov.hmcts.probate.model.ccd.raw.DeathRecord value = deathRecordCollectionMembers.get(0).getValue();

        assertEquals(value.getSystemNumber(), v1Death.getId());
        assertEquals(value.getName(), String.format("%s %s", deceased.getForenames(), deceased.getSurname()));
        assertEquals(value.getDateOfBirth(), deceased.getDateOfBirth());
        assertEquals(value.getDateOfDeath(), deceased.getDateOfDeath());
        assertEquals(value.getSex(), deceased.getSex().getValue());
        assertEquals(value.getAddress(), deceased.getAddress());
    }
}
