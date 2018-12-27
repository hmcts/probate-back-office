package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/probateManTypes")
public class ProbateManController {

    @PostMapping(path = "/grantApplication/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> saveGrantApplicationToCcd(@PathVariable("id") String id) {
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }

    @PostMapping(path = "/caveat/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> saveCaveatToCcd(@PathVariable("id") String id) {
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }

    @PostMapping(path = "/admonWill/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> saveAdmonWillToCcd(@PathVariable("id") String id) {
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }

    @PostMapping(path = "/standingSearch/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> saveStandingSearchToCcd(@PathVariable("id") String id) {
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }
}
