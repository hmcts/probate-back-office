package uk.gov.hmcts.probate.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class NameParserTest {

    public static final String SURNAME = "aliasSurname";
    public static final String FIRST_NAMES = "aliasFirstName";
    private NameParser nameParser;

    @Parameter
    public String aliasName;

    @Parameter(value = 1)
    public Map<String, String> expected;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        nameParser = new NameParser();
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Fred Bob Smith", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred Bob")
                        .put(SURNAME, "Smith")
                        .build()},
                {"Fred Bob-Smith", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred")
                        .put(SURNAME, "Bob-Smith")
                        .build()},
                {"Fred-Bob Smith", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred-Bob")
                        .put(SURNAME, "Smith")
                        .build()},
                {"Fred Bob Smith ", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred Bob")
                        .put(SURNAME, "Smith")
                        .build()},
                {"Fred  Bob  Smith ", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred  Bob")
                        .put(SURNAME, "Smith")
                        .build()},
                {"Fred, Bob, Smith ", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred, Bob,")
                        .put(SURNAME, "Smith")
                        .build()},
                {"FredBobSmith ", ImmutableMap.builder()
                        .put(FIRST_NAMES, "FredBobSmith")
                        .put(SURNAME, "")
                        .build()},
                {" Fred Bob Smith", ImmutableMap.builder()
                        .put(FIRST_NAMES, "Fred Bob")
                        .put(SURNAME, "Smith")
                        .build()},
        });
    }

    @Test
    public void shouldParseName() {
        String aliasNamesString = "alias";

        ObjectNode aliasNamesList = objectMapper.createObjectNode();
        aliasNamesList.put(aliasNamesString, new TextNode(aliasName));

        Map<String, String> objectNode = nameParser.parse(aliasName);

        String firstNames =
                objectNode.get(FIRST_NAMES) == null ? "" : objectNode.get(FIRST_NAMES);
        String surname =
                objectNode.get(SURNAME) == null ? "" : objectNode.get(SURNAME);

        assertThat(firstNames, is(equalTo(expected.get(FIRST_NAMES))));
        assertThat(surname, is(equalTo(expected.get(SURNAME))));
    }

}
