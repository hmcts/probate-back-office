package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.EmailWithFileService;
import uk.gov.hmcts.probate.service.filebuilder.FileExtractDateFormatter;
import uk.gov.hmcts.probate.service.filebuilder.HmrcFileService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HmrcDataExtractService {
    private final CaseQueryService caseQueryService;
    private final FileTransferService fileTransferService;
    private final HmrcFileService hmrcFileService;
    private final FileExtractDateFormatter fileExtractDateFormatter;

    private final EmailWithFileService emailWithFileService;

    public void performHmrcExtractFromDate(String fromDate, String toDate) {
        try {
            if (fromDate.equals(toDate)) {
                performHmrcExtract(fromDate);
            } else {
                log.info("HMRC data extract initiated for dates from-to: {}-{}", fromDate, toDate);

                List<ReturnedCaseDetails> casesFound =
                    caseQueryService.findCaseStateWithinDateRangeHMRC(fromDate, toDate);
                log.info("Cases found for HMRC data extract initiated for dates from-to: {}-{}, cases found: {}",
                    fromDate, toDate, casesFound.size());

                uploadHmrcFile(fromDate, toDate, casesFound);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private void performHmrcExtract(String date) {
        log.info("HMRC data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> casesFound = caseQueryService.findGrantIssuedCasesWithGrantIssuedDate("HMRC", date);
        log.info("Cases found for HMRC data extract initiated for date: {}, cases found: {}", date, casesFound.size());

        uploadHmrcFile(date, date, casesFound);
    }

    private void emailHmrcFile(String date, File hmrcFile) {

        boolean isSuccess = emailWithFileService.emailFile(hmrcFile, date);
        if (!isSuccess) {
            log.error("Failed to email hmrc file:" + hmrcFile.getName() + "for date " + date);
        }
    }

    private void uploadHmrcFile(String fromDate, String toDate, List<ReturnedCaseDetails> casesFound) {
        String dateDesc = " from " + fromDate + " to " + toDate;
        log.info("preparing for file HMRC upload");
        File hmrcFile = hmrcFileService.createHmrcFile(casesFound, buildFileName(fromDate));
        emailHmrcFile(dateDesc, hmrcFile);
        int response = fileTransferService.uploadFile(hmrcFile);

        log.info("Response for HMRC upload={}", response);
        if (response != 201) {
            log.error("Failed to upload HMRC file for :" + dateDesc);
            throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Failed to upload HMRC file for " + dateDesc);
        }
    }

    private String buildFileName(String toDate) {
        return "1_" + fileExtractDateFormatter.getHmrcFormattedFileDate(toDate, LocalDateTime.now()) + ".new";
    }

}
