package uk.gov.hmcts.probate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractService;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;

import java.util.concurrent.Executor;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;

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
    private final DataExtractService dataExtractService;
    private final Executor threadPoolTaskExecutor;

    @Operation(summary = "Initiate HMRC data extract within 2 dates",
            description = "Dates MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrc")
    public ResponseEntity<String> initiateHmrcExtractFromDate(
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform HMRC data extract from dates...");
        threadPoolTaskExecutor.execute(() ->
                hmrcDataExtractService.performHmrcExtractFromDate(fromDate, toDate)
        );
        log.info("Perform HMRC data extract from dates task submitted");

        return ResponseEntity.accepted().body("Perform HMRC data extract task submitted");
    }

    @Operation(summary = "Initiate IronMountain data extract with date",
            description = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain")
    public ResponseEntity<String> initiateIronMountainExtract(
            @Parameter(name = "Date to find cases against", required = true)
            @RequestParam("date") String date) {
        return executeIronMountainExtractForDate(date);
    }

    @Operation(summary = "Initiate IronMountain data extract with date",
        description = "Date MUST be in callbackRequest 'yyyy-MM-dd'")
    @PostMapping(path = "/resend-iron-mountain", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> initiateIronMountainExtract(
        @RequestBody CallbackRequest callbackRequest) {
        String resendDate = callbackRequest.getCaseDetails().getData().getResendDate();
        CallbackResponse callbackResponse = CallbackResponse.builder().build();
        ResponseEntity responseEntity = executeIronMountainExtractForDate(resendDate);
        if (responseEntity.getBody().equals("Perform Iron Mountain data extract task submitted")) {
            return ResponseEntity.ok(callbackResponse);
        } else {
            return null;
        }
    }

    private ResponseEntity<String> executeIronMountainExtractForDate(String date) {
        dataExtractDateValidator.dateValidator(date);
        log.info("Calling perform Iron Mountain data extract from date {}", date);
        threadPoolTaskExecutor.execute(() ->
                ironMountainDataExtractService.performIronMountainExtractForDate(date)
        );
        log.info("Perform Iron Mountain data extract from date task submitted");
        return ResponseEntity.accepted().body("Perform Iron Mountain data extract task submitted");
    }

    @Operation(summary = "Initiate Exela data extract", description = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/exela")
    public ResponseEntity<String> initiateExelaExtractDateRange(
            @Parameter(name = "Date to find cases against", required = true)
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform Exela data extract from date...");
        threadPoolTaskExecutor.execute(() ->
                exelaDataExtractService.performExelaExtractForDateRange(fromDate, toDate)
        );
        log.info("Perform Exela data extract from date task submitted");

        return ResponseEntity.accepted().body("Exela data extract task submitted");
    }

    @Operation(summary = "Initiate Smee And Ford data extract", description = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/smee-and-ford")
    public ResponseEntity<Void> initiateSmeeAndFordExtract(
                @Parameter(name = "Date to find cases against", required = true)
                @RequestParam(value = "fromDate") String fromDate,
                @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform Smee And Ford data extract from date...");
        threadPoolTaskExecutor.execute(() ->
                smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange(fromDate, toDate)
        );
        log.info("Perform Smee And Ford data extract from date task submitted");

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Initiate NFI data extract", description = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/nfi")
    public ResponseEntity<Void> initiateNFIExtract(
            @Parameter(name = "Date to find cases against", required = true)
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Submitting NFI data extract task for fromDate={} toDate={}", fromDate, toDate);

        threadPoolTaskExecutor.execute(() ->
                dataExtractService.performExtractForDateRange(fromDate, toDate, NATIONAL_FRAUD_INITIATIVE)
        );
        log.info("Perform NFI data extract task submitted");

        return ResponseEntity.accepted().build();
    }
}
