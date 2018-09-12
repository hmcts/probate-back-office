package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.template.printservice.PrintService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/template")
public class PrintServiceTemplateController {

    private final PrintService printService;

    @PostMapping(path = "/documents", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List> getAllDocuments(@RequestBody CaseDetails caseDetails, BindingResult bindingResult) {

        log.info("POST /template/documents ", "Case id: {} ", caseDetails.getId());
        log.debug("POST /template/documents: {}", caseDetails);

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caseDetails.getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        List<DocumentResponse> docs = printService.getAllDocuments(caseDetails);


        return ResponseEntity.ok(docs);
    }

    @GetMapping(path = "/case-details/sol", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {TEXT_HTML_VALUE})
    public ResponseEntity<String> getSolicitorCaseDetailsTemplate() {

        String callbackResponse = printService.getSolicitorCaseDetailsTemplateForPrintService();
        return ResponseEntity.ok(callbackResponse);
    }

    @GetMapping(path = "/case-details/pa", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {TEXT_HTML_VALUE})
    public ResponseEntity<String> getPACaseDetailsTemplate() {

        String callbackResponse = printService.getPACaseDetailsTemplateForPrintService();
        return ResponseEntity.ok(callbackResponse);
    }
}
