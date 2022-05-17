package uk.gov.hmcts.probate.blob.component;


import org.junit.Test;

public class BlobUploadTest {

    BlobUpload blobUpload;

    @Test
    public void testUploadFile() {

        String file2 = "src/test/resources/expectedGeneratedFiles/hmrcMultipleCases.txt";
        String file3 = "src/test/resources/expectedGeneratedFiles/hmrcPersonal.txt";
        String file4 = "src/test/resources/expectedGeneratedFiles/hmrcSolicitor.txt";
        String[] filePaths = new String[] {file2, file3, file4};
        //blobUpload.uploadFile(filePaths);
    }

}
