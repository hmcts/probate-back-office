package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.filebuilder.FileExtractDateFormatter;
import uk.gov.hmcts.probate.service.filebuilder.IronMountainFileService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IronMountainDataExtractService {
    private final CaseQueryService caseQueryService;
    private final FileTransferService fileTransferService;
    private final IronMountainFileService ironMountainFileService;
    private final FileExtractDateFormatter fileExtractDateFormatter;

    public void performIronMountainExtractForDate(String date) {
        log.info("Iron Mountain data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Cases found for Iron Mountain: {}", cases.size());

        log.info("preparing for Iron Mountain file upload");
        int response = fileTransferService.uploadFile(ironMountainFileService.createIronMountainFile(
            cases, buildFileName(date)));

        if (response != 201) {
            log.error("Failed to upload Iron Mountain file for: " + date);
            throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to upload Iron Mountain file for date: " + date);
        }
    }

    private String buildFileName(String date) {
        return fileExtractDateFormatter.getIronMountainFormattedFileDate(date) + "grant.txt";
    }

}
