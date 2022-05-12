package uk.gov.hmcts.probate.service.zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ZipFileServiceTest {
    @Mock
    private EmUploadService emUploadService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ZipFileService zipFileService;

    private final List<ByteArrayResource> byteArrayResourceList = new ArrayList<>();
    private final List<ReturnedCaseDetails> returnedCaseDetails = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        returnedCaseDetails.add(getNewCaseData(1L));
        returnedCaseDetails.add(getNewCaseData(2L));
        returnedCaseDetails.add(getNewCaseData(3L));

        final ByteArrayResource byteArrayResource1 = new ByteArrayResource("firstFile".getBytes(UTF_8));
        final ByteArrayResource byteArrayResource2 = new ByteArrayResource("secondFile".getBytes(UTF_8));
        final ByteArrayResource byteArrayResource3 = new ByteArrayResource("thirdFile".getBytes(UTF_8));
        byteArrayResourceList.add(byteArrayResource1);
        byteArrayResourceList.add(byteArrayResource2);
        byteArrayResourceList.add(byteArrayResource3);

        when(emUploadService.getDocument(anyString())).thenReturn(byteArrayResource1, byteArrayResource2,
                byteArrayResource3);
    }

    private ReturnedCaseDetails getNewCaseData(Long caseId) {
        DocumentLink link = DocumentLink.builder().documentBinaryUrl("/documents/12345/binary").build();
        Document doc = Document.builder().documentType(DocumentType.DIGITAL_GRANT)
                .documentLink(link)
                .build();
        CollectionMember<Document> cm = new CollectionMember(doc);
        List<CollectionMember<Document>> cms = new ArrayList<>();
        cms.add(cm);
        CaseData data = CaseData.builder().probateDocumentsGenerated(cms).build();
        ReturnedCaseDetails returnedCaseDetails = new ReturnedCaseDetails(data, null, caseId);

        return returnedCaseDetails;
    }

    @Test
    public void shouldCreateZip() {
        zipFileService.generateZipFile(returnedCaseDetails, new File("temp"));
        //assertTrue(zip.getAbsolutePath().contains("multiCompressed"));
    }

}
