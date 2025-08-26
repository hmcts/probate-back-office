package uk.gov.hmcts.probate.service.lifeevents;

import com.github.hmcts.lifeevents.client.model.Deceased;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Alias;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AliasMapperTest {
    private final AliasMapper mapper = new AliasMapper();

    @Test
    void shouldReturnsEmptyListWhenAliasesNull() {
        Deceased deceased = new Deceased();
        deceased.setAliases(null);

        List<CollectionMember<Alias>> result = mapper.map(deceased);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void shouldReturnsEmptyListWhenAliasesEmpty() {
        Deceased deceased = new Deceased();
        deceased.setAliases(Collections.emptyList());

        List<CollectionMember<Alias>> result = mapper.map(deceased);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void shouldFiltersNullAndBlankAliases() {
        com.github.hmcts.lifeevents.client.model.Alias fullAlias = new com.github.hmcts.lifeevents.client.model.Alias();
        fullAlias.setPrefix("Dr");
        fullAlias.setForenames("Jane Mary");
        fullAlias.setSurname("Doe");
        fullAlias.setType("Maiden");
        fullAlias.setSuffix("PhD");

        com.github.hmcts.lifeevents.client.model.Alias blankAlias =
                new com.github.hmcts.lifeevents.client.model.Alias();
        blankAlias.setForenames("");
        blankAlias.setSurname("");

        com.github.hmcts.lifeevents.client.model.Alias surnameAlias =
                new com.github.hmcts.lifeevents.client.model.Alias();
        surnameAlias.setSurname("Smith");

        com.github.hmcts.lifeevents.client.model.Alias forenameAlias =
                new com.github.hmcts.lifeevents.client.model.Alias();
        forenameAlias.setForenames("J.");


        com.github.hmcts.lifeevents.client.model.Alias nullAlias = null;

        Deceased deceased = new Deceased();
        deceased.setAliases(java.util.Arrays.asList(
                fullAlias, nullAlias, blankAlias, surnameAlias, forenameAlias
        ));

        List<CollectionMember<Alias>> result = mapper.map(deceased);

        assertEquals(3, result.size());

        Alias m1 = result.get(0).getValue();
        assertAll("First alias",
                () -> assertEquals("Dr", m1.getPrefix()),
                () -> assertEquals("Jane Mary", m1.getForenames()),
                () -> assertEquals("Doe", m1.getLastName()),
                () -> assertEquals("Maiden", m1.getType()),
                () -> assertEquals("PhD", m1.getSuffix())
        );

        Alias m2 = result.get(1).getValue();
        assertAll("Second alias (surname only)",
                () -> assertNull(m2.getForenames()),
                () -> assertEquals("Smith", m2.getLastName()),
                () -> assertNull(m2.getPrefix()),
                () -> assertNull(m2.getType()),
                () -> assertNull(m2.getSuffix())
        );

        Alias m3 = result.get(2).getValue();
        assertAll("Third alias (forenames only)",
                () -> assertEquals("J.", m3.getForenames()),
                () -> assertNull(m3.getLastName()),
                () -> assertNull(m3.getPrefix()),
                () -> assertNull(m3.getType()),
                () -> assertNull(m3.getSuffix())
        );
    }
}
