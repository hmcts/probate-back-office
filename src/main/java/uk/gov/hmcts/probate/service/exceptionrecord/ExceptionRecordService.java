package uk.gov.hmcts.probate.service.exceptionrecord;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ExceptionRecordCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.CaveatCaseUpdateRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import uk.gov.hmcts.probate.model.exceptionrecord.ResponseCaveatDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulCaveatUpdateResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordCaveatMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordGrantOfRepresentationMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_EXPIRY_EXTENSION_PERIOD_IN_MONTHS;

@Slf4j
@Service
public class ExceptionRecordService {

    private static final String CAVEAT_EXTEND_CASE_REFERENCE_KEY = "caseReference";

    @Autowired
    List<CaveatsExpiryValidationRule> validationRuleCaveatsExpiry;

    @Autowired
    EventValidationService eventValidationService;

    @Autowired
    CaveatNotificationService caveatNotificationService;

    @Autowired
    ExceptionRecordCaveatMapper erCaveatMapper;

    @Autowired
    ExceptionRecordGrantOfRepresentationMapper erGrantOfRepresentationMapper;

    @Autowired
    ScannedDocumentMapper documentMapper;

    @Autowired
    CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    @Autowired
    CallbackResponseTransformer grantOfRepresentationTransformer;

    public SuccessfulTransformationResponse createCaveatCaseFromExceptionRecord(
            ExceptionRecordRequest erRequest,
            List<String> warnings) {

        List<String> errors = new ArrayList<String>();

        try {
            log.info("About to map Caveat OCR fields to CCD for case: {}", erRequest.getId());
            CaveatData caveatData = erCaveatMapper.toCcdData(erRequest.getOCRFieldsObject());

            // Add bulkScanReferenceId
            caveatData.setBulkScanCaseReference(erRequest.getId());

            // Add scanned documents
            log.info("About to map Caveat Scanned Documents to CCD.");
            caveatData.setScannedDocuments(erRequest.getScannedDocuments()
                    .stream()
                    .map(it -> documentMapper.toCaseDoc(it, erRequest.getId()))
                    .collect(toList()));

            log.info("Calling caveatTransformer to create transformation response for bulk scan orchestrator.");
            CaseCreationDetails caveatCaseDetailsResponse = caveatCallbackResponseTransformer.bulkScanCaveatCaseTransform(caveatData);

            return SuccessfulTransformationResponse.builder()
                    .caseCreationDetails(caveatCaseDetailsResponse)
                    .warnings(warnings)
                    .build();

        } catch (Exception e) {
            log.error("Error transforming Caveat case from Exception Record", e);
            throw new OCRMappingException(e.getMessage());
        }
    }

    public SuccessfulTransformationResponse createGrantOfRepresentationCaseFromExceptionRecord(
            ExceptionRecordRequest erRequest,
            GrantType grantType,
            List<String> warnings) {

        List<String> errors = new ArrayList<String>();

        try {
            log.info("About to map Grant of Representation OCR fields to CCD for case: {}", erRequest.getId());
            GrantOfRepresentationData grantOfRepresentationData = erGrantOfRepresentationMapper
                .toCcdData(erRequest.getOCRFieldsObject(), grantType);

            // Add bulkScanReferenceId
            grantOfRepresentationData.setBulkScanCaseReference(erRequest.getId());

            // Add scanned documents
            log.info("About to map Grant of Representation Scanned Documents to CCD.");
            grantOfRepresentationData.setScannedDocuments(erRequest.getScannedDocuments()
                    .stream()
                    .map(it -> documentMapper.toCaseDoc(it, erRequest.getId()))
                    .collect(toList()));

            // Add grant type
            grantOfRepresentationData.setGrantType(grantType);

            log.info("Calling grantOfRepresentationTransformer to create transformation response for bulk scan orchestrator.");
            CaseCreationDetails grantOfRepresentationCaseDetailsResponse =
                    grantOfRepresentationTransformer.bulkScanGrantOfRepresentationCaseTransform(grantOfRepresentationData);

            return SuccessfulTransformationResponse.builder()
                    .caseCreationDetails(grantOfRepresentationCaseDetailsResponse)
                    .warnings(warnings)
                    .build();

        } catch (Exception e) {
            log.error("Error transforming Grant of Representation case from Exception Record", e);
            throw new OCRMappingException(e.getMessage());
        }
    }

    public SuccessfulCaveatUpdateResponse updateCaveatCaseFromExceptionRecord(
            CaveatCaseUpdateRequest erCaseUpdateRequest) {

        List<String> errors = new ArrayList<String>();
        ExceptionRecordRequest erRequest = erCaseUpdateRequest.getExceptionRecord();
        ExceptionRecordCaveatDetails exceptionRecordCaveatDetails = erCaseUpdateRequest.getCaveatDetails();
        CaveatDetails caveatDetails = new CaveatDetails(exceptionRecordCaveatDetails.getData(), null, exceptionRecordCaveatDetails.getId());
        HashMap<String, String> ocrFieldValues = new HashMap<String, String>();
        List<OCRField> ocrFields = erRequest.getOcrFields();
        String caseReference = null;

        ocrFields.forEach(ocrField -> {
            ocrFieldValues.put(ocrField.getName(), ocrField.getValue());
        });

        try {
            log.info("About to update Caveat expiry date extention for case: {}", erRequest.getId());

            if (StringUtils.isNotBlank(ocrFieldValues.get(CAVEAT_EXTEND_CASE_REFERENCE_KEY))
                    && (StringUtils.isNotBlank(caveatDetails.getId().toString()))) {
                caseReference = ocrFieldValues.get(CAVEAT_EXTEND_CASE_REFERENCE_KEY);
            }

            Assert.notEmpty(
                    erRequest.getScannedDocuments(),
                    "Missing scanned documents in Exception Record"
            );

            // Create CaveatCallbackRequest
            CaveatCallbackRequest caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

            // Add scanned documents
            log.info("Mapping Caveat Scanned Documents to case.");
            uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData caveatData = caveatDetails.getData();
            int originalScannedNumber = caveatCallbackRequest.getCaseDetails().getData().getScannedDocuments().size();
            caveatCallbackRequest.getCaseDetails().getData().setScannedDocuments(
                    mergeScannedDocuments(caveatData.getScannedDocuments(), erRequest.getScannedDocuments(), erRequest.getId()));

            Assert.isTrue(
                originalScannedNumber < caveatDetails.getData().getScannedDocuments().size(),
                "Number of scanned documents has not increased"
            );

            // Validate caveat extension
            log.info("Validating caveat extension.");
            CaveatCallbackResponse caveatCallbackResponse = eventValidationService
                .validateCaveatRequest(caveatCallbackRequest, validationRuleCaveatsExpiry);
            if (caveatCallbackResponse.getErrors().isEmpty()) { 
                LocalDate defaultExpiry = caveatCallbackRequest.getCaseDetails().getData()
                        .getExpiryDate().plusMonths(CAVEAT_EXPIRY_EXTENSION_PERIOD_IN_MONTHS);
                log.info("No errors found with validateCaveatRequest, updating expiryDate to {} in request.",
                        defaultExpiry.format(CaveatCallbackResponseTransformer.dateTimeFormatter));
                caveatCallbackRequest.getCaseDetails().getData().setExpiryDate(defaultExpiry);
                log.info("Calling caveatExtend to notify of caveator of extension.");
                caveatCallbackResponse = caveatNotificationService.caveatExtend(caveatCallbackRequest);
                if (!caveatDetails.getId().toString().equals(caseReference)) {
                    if (caveatCallbackResponse.getWarnings() == null) {
                        caveatCallbackResponse.setWarnings(new ArrayList());
                    }
                    caveatCallbackResponse.getWarnings().add("Case retrieved does not match OCR data for caseReference");
                }

                log.info("Call to caveatExtend was successful.");
            } else {
                throw new OCRMappingException(caveatCallbackResponse.getErrors().get(0));
            }

            return SuccessfulCaveatUpdateResponse.builder()
                    .caseUpdateDetails(ResponseCaveatDetails.builder().caseData(caveatCallbackResponse.getCaveatData()).build())
                    .warnings(caveatCallbackResponse.getWarnings())
                    .build();

        } catch (Exception e) {
            log.error("Error Extending Caveat case from Exception Record", e);
            throw new OCRMappingException(e.getMessage());
        }
    }

    private List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument>>
    mergeScannedDocuments(List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument>>
                                  caseScannedDocuments, List<InputScannedDoc> exceptionScannedDocuments, String exceptionRecordReference) {
        log.info("About to merge Caveat Scanned Documents to existing case.");
        List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument>> newScannedDocuments;
        if (caseScannedDocuments == null) {
            newScannedDocuments = new ArrayList<>();
        } else {
            newScannedDocuments = new ArrayList<>(caseScannedDocuments);
        }

        exceptionScannedDocuments.forEach(newScannedDoc -> {
            AtomicBoolean foundDoc = new AtomicBoolean(false);
            caseScannedDocuments.forEach(caseScannedDoc -> {
                if (StringUtils.isNotBlank(newScannedDoc.controlNumber)
                    && newScannedDoc.controlNumber.equalsIgnoreCase(caseScannedDoc.getValue().getControlNumber())) {
                    foundDoc.set(true);
                }
            }
            );

            if (!foundDoc.get()) {
                log.info("Adding document with DCN {} to case", newScannedDoc.controlNumber);
                newScannedDocuments.add(documentMapper.updateCaseDoc(newScannedDoc, exceptionRecordReference));
            } else {
                log.warn("Skipping adding document to case as the DCN {} already exists", newScannedDoc.controlNumber);
            }
        });

        return newScannedDocuments;
    }
}
