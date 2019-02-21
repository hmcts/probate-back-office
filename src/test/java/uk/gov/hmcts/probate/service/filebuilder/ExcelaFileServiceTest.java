package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.Case;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExcelaFileServiceTest {

    private CaseData.CaseDataBuilder caseData;
    private ExcelaFileService excelaFileService = new ExcelaFileService();
    private Case testCase;
    private static final String FILE_NAME = "testFile.txt";

    @Before
    public void setup() {
        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(ScannedDocument
                .builder().subtype("will").controlNumber("123456").build());
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>(1);
        scannedDocuments.add(scannedDocument);

        caseData = CaseData.builder()
                .deceasedSurname("Michelson")
                .scannedDocuments(scannedDocuments);

        testCase = new Case(caseData.build(), 123L);
    }

    @Test
    public void testExcelaFileBuilt() throws IOException {
        assertThat(createFile(excelaFileService.createExcelaFile(testCase, FILE_NAME)).readLine(),
                is("123,Michelson,123456"));
    }

    private BufferedReader createFile(File file) throws FileNotFoundException {
        file.deleteOnExit();
        FileReader fileReader = new FileReader(file);
        return new BufferedReader(fileReader);
    }
}
