package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.ProbateManService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProbateManController {

    private ProbateManService probateManService;

    @PostMapping(path = "/probateManTypes/{probateManType}/cases/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> saveGrantApplicationToCcd(@PathVariable("probateManType") ProbateManType probateManType,
                                                                      @PathVariable("id") String id) {
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }
}
