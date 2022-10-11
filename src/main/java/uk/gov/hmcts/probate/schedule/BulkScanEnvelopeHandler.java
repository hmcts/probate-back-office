package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bulkscan.enums.EnvelopeProcessStatus;
import uk.gov.hmcts.bulkscan.type.BulkScanEnvelopeProcessingResponse;
import uk.gov.hmcts.bulkscan.type.Classification;
import uk.gov.hmcts.bulkscan.type.IEnvelopeReceiver;
import uk.gov.hmcts.bulkscan.type.ProcessedEnvelopeContents;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.exceptionrecord.ExceptionRecordService;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.service.ocr.FormType;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;
import uk.gov.hmcts.reform.probate.model.ScannedDocument;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Component
@RequiredArgsConstructor
@Slf4j
public class BulkScanEnvelopeHandler implements IEnvelopeReceiver {

    private final DocumentManagementService documentManagementService;
    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;
    private final OCRPopulatedValueMapper ocrPopulatedValueMapper;
    private final ExceptionRecordService erService;
    private final ScannedDocumentMapper documentMapper;

    @Override
    public BulkScanEnvelopeProcessingResponse onEnvelopeReceived(ProcessedEnvelopeContents envelopeContents) {

        // upload the pdfs, attach to a case etc
        log.info("Uploading contents of {}", envelopeContents.getEnvelope().getFileName());
        var uploadedPdfs = new HashMap<String, CollectionMember<ScannedDocument>>();
        try {
            Map<String, File> files = envelopeContents.getExtractedFiles();
            // upload files to document management
            envelopeContents.getInputEnvelope().scannableItems.forEach(scannableItem -> {
                UploadResponse uploadResponse = null;
                var file = files.get(scannableItem.fileName);
                if (file != null) {
                    try {
                        uploadResponse = documentManagementService.upload(
                                getEmFile(Files.readAllBytes(file.toPath())),
                                DocumentType.OTHER
                        );

                        log.info("Uploading file {}", scannableItem.fileName);

                        // need to tie together InputScannableItem and uploadedPdfs
                        uploadedPdfs.put(scannableItem.fileName, documentMapper.toCaseDoc(
                                scannableItem,
                                uploadResponse,
                                envelopeContents.getInputEnvelope()
                        ));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        var caseNumber = envelopeContents.getInputEnvelope().caseNumber;
        var inputFormType = envelopeContents.getInputEnvelope().getFormType();
        FormType.isFormTypeValid(inputFormType);
        var formType = FormType.valueOf(inputFormType);
        var caseData = upsertCase(envelopeContents, formType, uploadedPdfs);

        envelopeContents.getInputEnvelope().payments.forEach(inputPayment -> {
            log.info("Process inputPayment DCN {}", inputPayment.documentControlNumber);
        });

        return new BulkScanEnvelopeProcessingResponse(
                envelopeContents.getEnvelope().getEtag(),
                String.format("Successfully processed Envelope %s", envelopeContents.getEnvelope().getEtag()),
                EnvelopeProcessStatus.SUCCESS,
                emptyList(),
                emptyList()
        );

    }

    private CaseCreationDetails upsertCase(ProcessedEnvelopeContents processedEnvelopeContents,
                                           FormType formType,
                                           HashMap<String, CollectionMember<ScannedDocument>> uploadedPdfs) {

        var caseNumber = processedEnvelopeContents.getInputEnvelope().caseNumber;
        var classification = processedEnvelopeContents.getInputEnvelope().classification;
        var ocrData = processedEnvelopeContents.getInputEnvelope().retrieveOcrDataFields();

        log.info("Transform exception record data for form type: {}, case: {}", formType, caseNumber);


        List<String> warnings = ocrToCCDMandatoryField
                .ocrToCCDMandatoryFields(ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrData), formType);

        if (!warnings.isEmpty()) {
            log.warn(warnings.toString());
            throw new OCRMappingException("Please resolve all warnings before creating the case", warnings);
        }

        if (!classification.equals(Classification.NEW_APPLICATION)) {
            throw new OCRMappingException("This Exception Record can not be created as a case: "
                    + caseNumber + " as it is not a " + Classification.NEW_APPLICATION);
        }

        log.info("Validation check passed, attempting to transform case for form-type {}, caseId {}", formType,
                caseNumber);

        return switch (formType) {
            case PA8A -> erService.createCaveatCaseFromExceptionRecord(
                    processedEnvelopeContents.getInputEnvelope().caseNumber,
                    ExceptionRecordRequest.getOCRFieldsObject(
                            processedEnvelopeContents.getInputEnvelope().retrieveOcrDataFields()
                    ),
                    uploadedPdfs);
            case PA1P -> erService.createGrantOfRepresentationCaseFromExceptionRecord(
                    processedEnvelopeContents.getInputEnvelope().caseNumber,
                    ExceptionRecordRequest.getOCRFieldsObject(
                            processedEnvelopeContents.getInputEnvelope().retrieveOcrDataFields()
                    ),
                    GrantType.GRANT_OF_PROBATE,
                    uploadedPdfs);
            case PA1A -> erService.createGrantOfRepresentationCaseFromExceptionRecord(
                    processedEnvelopeContents.getInputEnvelope().caseNumber,
                    ExceptionRecordRequest.getOCRFieldsObject(
                            processedEnvelopeContents.getInputEnvelope().retrieveOcrDataFields()
                    ),
                    GrantType.INTESTACY,
                    uploadedPdfs);
            default -> throw new OCRMappingException(
                    "This Exception Record form currently has no case mapping for case "
                            + caseNumber);
        };
    }

    private EvidenceManagementFileUpload getEmFile(byte[] bytes) {
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);
    }
}
