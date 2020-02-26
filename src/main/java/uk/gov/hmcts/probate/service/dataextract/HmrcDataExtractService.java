package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.filebuilder.FileExtractDateFormatter;
import uk.gov.hmcts.probate.service.filebuilder.HmrcFileService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HmrcDataExtractService {
    private final CaseQueryService caseQueryService;
    private final FileTransferService fileTransferService;
    private final HmrcFileService hmrcFileService;
    private final FileExtractDateFormatter fileExtractDateFormatter;

    public void performHmrcExtract(String date) {
        log.info("HMRC data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> casesFound = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Cases found for HMRC data extract initiated for date: {}, cases found: {}", date, casesFound.size());

        if (!casesFound.isEmpty()) {
            uploadHmrcFile(null, date, casesFound);
        }
    }

    public void performHmrcExtractFromDate(String fromDate, String toDate) {
        log.info("HMRC data extract initiated for dates from-to: {}-{}", fromDate, toDate);

        List<ReturnedCaseDetails> casesFound = caseQueryService.findCaseStateWithinTimeFrame(fromDate, toDate);
        log.info("Cases found for HMRC data extract initiated for dates from-to: {}-{}, cases found: {}", 
            fromDate, toDate, casesFound.size());

        if (!casesFound.isEmpty()) {
            uploadHmrcFile(fromDate, toDate, casesFound);
        }
    }

    private void uploadHmrcFile(String fromDate, String date, List<ReturnedCaseDetails> casesFound) {
        String dateDesc = (StringUtils.isEmpty(fromDate) ? " date:" : " from " + fromDate + " to") + " " + date;
        log.info("preparing for file HMRC upload");
        int response = fileTransferService.uploadFile(hmrcFileService.createHmrcFile(
            casesFound, "1_" + fileExtractDateFormatter.formatFileDate() + ".dat"));

        log.info("Response for HMRC upload={}", response);
        if (response != 201) {
            log.error("Failed to upload HMRC file for :" + dateDesc);
            throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to upload HMRC file for " + dateDesc);
        }
    }

}
