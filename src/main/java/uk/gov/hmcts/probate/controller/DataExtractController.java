package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.filebuilder.IronMountainFileService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data-extract")
@RestController
@Api(tags = "Initiate data extract for IronMountain and Excela")
public class DataExtractController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final FileTransferService fileTransferService;
    private final IronMountainFileService ironMountainFileService;
    private final ExcelaCriteriaService excelaCriteriaService;

    @Scheduled(cron = "${cron.data_extract}")
    @ApiOperation(value = "Initiate IronMountain data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/iron-mountain")
    public ResponseEntity initiateIronMountainExtract() {
        log.info("Extract initiated for Iron Mountain");
        return initiateIronMountainExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate IronMountain data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain/{date}")
    public ResponseEntity initiateIronMountainExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                      @PathVariable("date") String date) {
        dateValidator(date);
        log.info("Iron Mountain data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Cases found for Iron Mountain: {}", cases.size());

        if (!cases.isEmpty()) {
            log.info("preparing for file upload");
            int response = fileTransferService.uploadFile(ironMountainFileService.createIronMountainFile(
                    cases, date.replace("-", "") + "grant.txt"));

            if (response != 201) {
                log.error("Failed to upload file for: " + date);
                throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to upload file for date: " + date);
            }
        }
        return ResponseEntity.ok(cases.size() + " cases successfully found for date: " + date);
    }

    @Scheduled(cron = "${cron.data_extract}")
    @ApiOperation(value = "Initiate Excela data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/excela")
    public ResponseEntity initiateExcelaExtract() throws NotificationClientException {
        log.info("Extract initiated for Excela");
        return initiateExcelaExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate Excela data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/excela/{date}")
    public ResponseEntity initiateExcelaExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                @PathVariable("date") String date) throws NotificationClientException {
        dateValidator(date);
        log.info("Excela data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        List<ReturnedCaseDetails> filteredCases = excelaCriteriaService.getFilteredCases(cases);

        if (!filteredCases.isEmpty()) {
            log.info("Sending email to Excela");
            notificationService.sendExcelaEmail(filteredCases);
        }

        return ResponseEntity.ok(filteredCases.size() + " cases found and emailed for date: " + date);
    }

    private void dateValidator(String date) {
        try {
            LocalDate.parse(date, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.error("Error parsing date, use the format of 'yyyy-MM-dd': ");
            throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                    "Error parsing date, use the format of 'yyyy-MM-dd': " + e.getMessage());
        }
    }
}
