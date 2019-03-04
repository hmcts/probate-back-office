package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TextFileBuilderServiceTest {

    private TextFileBuilderService textFileBuilderService;
    private List<String> data;

    @Before
    public void setup() {
        textFileBuilderService = new TextFileBuilderService();

        data = new LinkedList<>();
        data.add("Bob");
        data.add("Smith");
    }

    @Test
    public void testFileContentsMatch() throws IOException {
        assertThat(createFile("testFile.txt", false).readLine(), is("Bob|Smith|"));
    }

    @Test
    public void testEmptyListItemsDisplayDelimiter() throws IOException {
        data.add("");
        data.add("");

        assertThat(createFile("testFile.txt", false).readLine(), is("Bob|Smith|||"));
    }

    @Test
    public void testFileNameIsSanitised() throws IOException {
        assertThat(createFile("te/st/F/il///e.//tx/t", false).readLine(), is("Bob|Smith|"));
    }

    @Test
    public void testFileIsTrimmed() throws IOException {
        assertThat(createFile("testFile.txt", true).readLine(), is("Bob|Smith"));
    }

    private BufferedReader createFile(String fileName, boolean trim) throws IOException {
        File file = textFileBuilderService.createFile(data, "|", fileName, trim);
        file.deleteOnExit();
        FileReader reader = new FileReader(file);
        return new BufferedReader(reader);
    }
}