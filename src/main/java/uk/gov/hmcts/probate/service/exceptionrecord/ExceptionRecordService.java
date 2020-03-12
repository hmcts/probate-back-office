package uk.gov.hmcts.probate.service.exceptionrecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseUpdateRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulUpdateResponse;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordCaveatMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordGrantOfRepresentationMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ExceptionRecordService {

    @Autowired
    ExceptionRecordCaveatMapper erCaveatMapper;

    @Autowired
    ExceptionRecordGrantOfRepresentationMapper erGrantOfRepresentationMapper;

    @Autowired
    ScannedDocumentMapper documentMapper;

    @Autowired
    CaveatCallbackResponseTransformer caveatTransformer;

    @Autowired
    CallbackResponseTransformer grantOfRepresentationTransformer;

    public SuccessfulTransformationResponse createCaveatCaseFromExceptionRecord(
            ExceptionRecordRequest erRequest,
            List<String> warnings) {

        List<String> errors = new ArrayList<String>();

        try {
            log.info("About to map Caveat OCR fields to CCD.");
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
            CaseCreationDetails caveatCaseDetailsResponse = caveatTransformer.bulkScanCaveatCaseTransform(caveatData);

            return SuccessfulTransformationResponse.builder()
                    .caseCreationDetails(caveatCaseDetailsResponse)
                    .warnings(warnings)
                    .errors(errors)
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
            log.info("About to map Grant of Representation OCR fields to CCD.");
            GrantOfRepresentationData grantOfRepresentationData = erGrantOfRepresentationMapper.toCcdData(erRequest.getOCRFieldsObject(), grantType);

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
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            log.error("Error transforming Grant of Representation case from Exception Record", e);
            throw new OCRMappingException(e.getMessage());
        }
    }

    public SuccessfulUpdateResponse updateCaveatCaseFromExceptionRecord(
            CaseUpdateRequest erCaseUpdateRequest) {

        List<String> errors = new ArrayList<String>();

        try {
            log.info("About to update Caveat expiry date extention.");
            ExceptionRecordRequest erRequest = erCaseUpdateRequest.getExceptionRecord();

            CaveatCallbackRequest caveatCallbackRequest = CaveatCallbackRequest

            // Transform case data
            ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = caveatTransformer.getResponseCaveatData(caveatCallbackRequest.getCaseDetails());



            CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponseTransformer.transformResponseWithNoChanges(erCaseUpdateRequest.getCaseDetails()) {
                ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = getResponseCaveatData(caveatCallbackRequest.getCaseDetails());

                return transformResponse(responseCaseDataBuilder.build());
            }



            // Add scanned documents
            log.info("About to map Caveat Scanned Documents to CCD.");
            caveatData.setScannedDocuments(erRequest.getScannedDocuments()
                    .stream()
                    .map(it -> documentMapper.toCaseDoc(it, erRequest.getId()))
                    .collect(toList()));

            log.info("Calling caveatTransformer to create transformation response for bulk scan orchestrator.");
            ResponseCaseDeta caveatCaseDataResponse = caveatTransformer.trans.bulkScanCaveatCaseTransform(caveatData);

            return SuccessfulUpdateResponse.builder()
                    .caseUpdateDetails(caveatCaseDetailsResponse)
                    .warnings(warnings)
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            log.error("Error transforming Caveat case from Exception Record", e);
            throw new OCRMappingException(e.getMessage());
        }
    }

}
