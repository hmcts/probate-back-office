package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class TextFileBuilderServiceTest {

    private TextFileBuilderService textFileBuilderService;
    private List<String> data;

    @BeforeEach
    public void setup() {
        textFileBuilderService = new TextFileBuilderService();

        data = new LinkedList<>();
        data.add("Bob");
        data.add("Smith");
    }

    @Test
    void testFileContentsMatch() throws IOException {
        assertThat(createFile("testFile.txt").readLine(), is("Bob|Smith|"));
    }

    @Test
    void testEmptyListItemsDisplayDelimiter() throws IOException {
        data.add("");
        data.add("");

        assertThat(createFile("testFile.txt").readLine(), is("Bob|Smith|||"));
    }

    @Test
    void testFileNameIsSanitised() throws IOException {
        assertThat(createFile("te/st/F/il///e.//tx/t").readLine(), is("Bob|Smith|"));
    }

    private BufferedReader createFile(String fileName) throws IOException {
        File file = textFileBuilderService.createFile(data, "|", fileName, "TEST");
        file.deleteOnExit();
        FileReader reader = new FileReader(file);
        return new BufferedReader(reader);
    }
}
