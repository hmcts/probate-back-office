package uk.gov.hmcts.probate.controller;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;
import uk.gov.service.notify.NotificationClientException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data-extract")
@RestController
@Tag(name = "Initiate data extract for HMRC, IronMountain and Exela")
public class DataExtractController {

    private final HmrcDataExtractService hmrcDataExtractService;
    private final IronMountainDataExtractService ironMountainDataExtractService;
    private final ExelaDataExtractService exelaDataExtractService;
    private final SmeeAndFordDataExtractService smeeAndFordDataExtractService;
    private final DataExtractDateValidator dataExtractDateValidator;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Initiate HMRC data extract within 2 dates",
            description = "Dates MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrc")
    public ResponseEntity initiateHmrcExtractFromDate(@RequestParam(value = "fromDate") String fromDate,
                                                      @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform HMRC data extract from dates...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            hmrcDataExtractService.performHmrcExtractFromDate(fromDate, toDate);
        });
        log.info("Perform HMRC data extract from dates finished");

        return ResponseEntity.accepted().body("Perform HMRC data extract finished");
    }

    @Operation(summary = "Initiate IronMountain data extract with date",
            description = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain")
    public ResponseEntity initiateIronMountainExtract(@Parameter(name = "Date to find cases against", required = true)
                                                      @RequestParam("date") String date) {
        dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform Iron Mountain data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            ironMountainDataExtractService.performIronMountainExtractForDate(date);
        });
        log.info("Perform Iron Mountain data extract from date finished");

        return ResponseEntity.accepted().body("Perform Iron Mountain data extract finished");
    }

    @Operation(summary = "Initiate IronMountain data extract with date",
        description = "Date MUST be in callbackRequest 'yyyy-MM-dd'")
    @PostMapping(path = "/resend-iron-mountain")
    public ResponseEntity initiateIronMountainExtract(
       // @RequestHeader(value = "Authorization") String authToken,
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) throws NotificationClientException {
        logRequest(request.getRequestURI(), callbackRequest);
        CaseData caseData = callbackRequest.getCaseDetails().getData();

    /*    dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform Iron Mountain data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            ironMountainDataExtractService.performIronMountainExtractForDate(date);
        });
        log.info("Perform Iron Mountain data extract from date finished");
*/

        return ResponseEntity.accepted().body("Perform Iron Mountain data extract finished");
    }


    @Operation(summary = "Initiate Exela data extract", description = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/exela")
    public ResponseEntity initiateExelaExtractDateRange(@Parameter(name = "Date to find cases against", required = true)
                                               @RequestParam("fromDate") String fromDate,
                                                @RequestParam("toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform Exela data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            exelaDataExtractService.performExelaExtractForDateRange(fromDate, toDate);
        });
        log.info("Perform Exela data extract from date finished");

        return ResponseEntity.accepted().body("Exela data extract finished");
    }

    @Operation(summary = "Initiate Smee And Ford data extract", description = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/smee-and-ford")
    public ResponseEntity initiateSmeeAndFordExtract(
                @Parameter(name = "Date to find cases against", required = true)
                @RequestParam(value = "fromDate") String fromDate,
                @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform Smee And Ford data extract from date...");
        smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange(fromDate, toDate);
        log.info("Perform Smee And Ford data extract from date finished");

        return ResponseEntity.accepted().body(null);
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            if (log.isDebugEnabled()) {
                log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
            }
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }
}
