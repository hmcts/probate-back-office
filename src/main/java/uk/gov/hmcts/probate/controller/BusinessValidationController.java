package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.BusinessValidationResponse;
import uk.gov.hmcts.probate.model.BusinessValidationStatus;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
public class BusinessValidationController {

    private static final Logger log = LoggerFactory.getLogger(BusinessValidationController.class);
    private final BusinessValidationService businessValidationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public BusinessValidationController(BusinessValidationService businessValidationService) {
        this.businessValidationService = businessValidationService;
    }

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BusinessValidationResponse validate(@Valid @RequestBody CCDData ccdData, BindingResult bindingResult) {
        try {
            log.debug("POST /validate: {}", mapper.writeValueAsString(ccdData));
        } catch (JsonProcessingException e) {
            log.error("POST /validate", e);
        }

        if (bindingResult.hasErrors()) {
            return new BusinessValidationResponse(BusinessValidationStatus.FAILURE,
                bindingResult.getFieldErrors(),
                Collections.emptyList());
        }

        List<BusinessValidationError> businessErrors = businessValidationService.validateForm(ccdData);
        if (!businessErrors.isEmpty()) {
            return new BusinessValidationResponse(BusinessValidationStatus.FAILURE,
                Collections.emptyList(),
                businessErrors);
        }

        return new BusinessValidationResponse(BusinessValidationStatus.SUCCESS);
    }
}
