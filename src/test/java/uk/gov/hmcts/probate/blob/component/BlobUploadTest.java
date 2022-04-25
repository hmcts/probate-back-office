package uk.gov.hmcts.probate.blob.component;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlobUploadTest {

    @Autowired
    BlobUpload blobUpload;

    @Test
    public void testUploadFile() throws IOException {

        String file2 = "src/test/resources/expectedGeneratedFiles/hmrcMultipleCases.txt";
        String file3 = "src/test/resources/expectedGeneratedFiles/hmrcPersonal.txt";
        String file4 = "src/test/resources/expectedGeneratedFiles/hmrcSolicitor.txt";
        String[] filePaths = new String[] {file2, file3, file4};
        //blobUpload.uploadFile(filePaths);
    }

}
