package uk.gov.hmcts.probate.service.exceptionrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordCaveatMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ExceptionRecordService {

    @Autowired
    ExceptionRecordCaveatMapper erCaveatMapper;

    @Autowired
    ScannedDocumentMapper documentMapper;

    @Autowired
    CaveatCallbackResponseTransformer caveatTransformer;

    public SuccessfulTransformationResponse createCaveatCaseFromExceptionRecord(
            ExceptionRecordRequest erRequest,
            List<String> warnings) {

        List<String> errors = new ArrayList<String>();

        try {
            CaveatData caveatData = erCaveatMapper.toCcdData(erRequest.getOCRFieldsObject());

            // Add scanned documents
            caveatData.setScannedDocuments(erRequest.getScannedDocuments()
                    .stream()
                    .map(it -> documentMapper.toCaseDoc(it, erRequest.getId()))
                    .collect(toList()));

            CaseCreationDetails caveatCaseDetailsResponse = caveatTransformer.newCaveatCaseTransform(caveatData);

            return SuccessfulTransformationResponse.builder()
                    .caseCreationDetails(caveatCaseDetailsResponse)
                    .warnings(warnings)
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            throw new OCRMappingException(e.getMessage());
        }
    }

}
