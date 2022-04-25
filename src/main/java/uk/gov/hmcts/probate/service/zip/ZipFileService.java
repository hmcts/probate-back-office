package uk.gov.hmcts.probate.service.zip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipFileService {
    private final EmUploadService emUploadService;

    public File zipIssuedGrants(List<ReturnedCaseDetails> cases) {
        File zip = null;
        DocumentType documentType = DocumentType.DIGITAL_GRANT;
        log.info("zipIssuedGrants for {} cases", cases.size());
        List<ByteArrayResource> filesToZip = new ArrayList<>();
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            List<CollectionMember<Document>> collect = returnedCaseDetails.getData()
                    .getProbateDocumentsGenerated().stream()
                    .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(documentType))
                    .collect(Collectors.toList());

            log.info("{} docs for case {}", collect.size(), returnedCaseDetails.getId());
            for (CollectionMember<Document> doc : collect) {
                String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                log.info("url:" + url);
                String id = url.substring(url.indexOf("/documents/") + 11, url.lastIndexOf("/"));
                log.info("id:" + id);
                ByteArrayResource file = emUploadService.getDocument(id);
                filesToZip.add(file);
                log.info("file:" + file.toString());
            }
        }
        try {
            zip = zipMultipleDocs(filesToZip);
        } catch (IOException e) {
            log.info("error: {}", e);
            throw new RuntimeException(e);
        }
        return zip;
    }

    private File zipMultipleDocs(List<ByteArrayResource> byteArrayResources) throws IOException {
        Path secureDir = Files.createTempDirectory("zip");
        log.info("secureDir:" + secureDir.toAbsolutePath().toString());
        Path tempFile = Files.createFile(
                Paths.get(secureDir.toAbsolutePath().toString() + "/multiCompressed.zip"));
        log.info("tempFile:" + tempFile.toAbsolutePath().toString());
        //secureDir.toFile().deleteOnExit();
        //tempFile.toFile().deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile.toFile());

        ZipOutputStream zipOut = new ZipOutputStream(fos);
        int i = 0;
        for (ByteArrayResource byteArrayResource :byteArrayResources) {
            log.info("byteArrayResource: {}", byteArrayResource.contentLength());
            ZipEntry zipEntry = new ZipEntry("fileName_" + i + ".pdf");
            zipOut.putNextEntry(zipEntry);

            log.info("zipMultipleDocs 1");
            zipOut.write(byteArrayResource.getByteArray());
            i++;
        }
        log.info("zipMultipleDocs 3");
        zipOut.close();
        log.info("zipMultipleDocs 4");
        fos.close();
        log.info("zipMultipleDocs 5");

        return tempFile.toFile();
    }
}
