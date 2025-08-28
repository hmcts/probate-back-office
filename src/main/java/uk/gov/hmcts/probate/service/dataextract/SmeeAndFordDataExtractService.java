package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.zip.ZipFileService;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Configuration
public class SmeeAndFordDataExtractService {
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final ZipFileService zipFileService;
    @Value("${feature.blobstorage.smeeandford.enabled}")
    public boolean featureBlobStorageSmeeAndFord;

    public void performSmeeAndFordExtractForDateRange(String fromDate, String toDate) {
        if (fromDate.equals(toDate)) {
            performSmeeAndFordExtractForDate(fromDate);
        } else {
            log.info("Smee And Ford data extract initiated from date: {} to {}", fromDate, toDate);
            List<ReturnedCaseDetails> cases = caseQueryService
                .findCaseStateWithinDateRangeSmeeAndFord(fromDate, toDate);
            log.info("Found {} cases with dated document for Smee And Ford from-to", cases.size());

            sendSmeeAndFordEmail(cases, fromDate, toDate);
        }
    }

    private void performSmeeAndFordExtractForDate(String date) {
        log.info("Smee And Ford data extract initiated for date: {}", date);
        List<ReturnedCaseDetails> cases = caseQueryService.findAllCasesWithGrantIssuedDate("Smee And Ford", date);
        log.info("Found {} cases with dated document for SF", cases.size());

        sendSmeeAndFordEmail(cases, date, date);
    }

    private void sendSmeeAndFordEmail(List<ReturnedCaseDetails> cases, String fromDate, String toDate) {
        log.info("Sending email to Smee And Ford for {} filtered cases", cases.size());
        if (!cases.isEmpty()) {
            try {
                log.info("FeatureBlobStorageSmeeAndFord flag enabled is {}", featureBlobStorageSmeeAndFord);
                if (featureBlobStorageSmeeAndFord) {
                    File tempFile = zipFileService.createTempZipFile("Probate_Docs_" + fromDate);
                    zipFileService.generateAndUploadZipFile(cases, tempFile, fromDate);
                    log.info("Zip file uploaded on blob store");
                    Files.delete(tempFile.toPath());
                }
                notificationService.sendSmeeAndFordEmail(cases, fromDate, toDate);
            } catch (NotificationClientException e) {
                log.warn("NotificationService exception sending email to Smee And Ford", e);
                throw new ClientException(HttpStatus.BAD_GATEWAY.value(),
                    "Error on NotificationService sending email to Smee And Ford");
            } catch (IOException e) {
                log.info("BlobUpload exception", e);
                throw new ClientException(HttpStatus.BAD_GATEWAY.value(),
                        "Blob upload exception for to Smee And Ford");
            }
        }

    }
}
