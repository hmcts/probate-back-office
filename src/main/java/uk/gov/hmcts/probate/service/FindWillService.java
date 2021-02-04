package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor

public class FindWillService {

    @Autowired
    private List<UploadDocument> listOfUploadedWills;
    private List<ScannedDocument> listOfScannedWills;
    private final PDFManagementService pdfManagementService;
    private ObjectMapper objectMapper;
    private uk.gov.hmcts.probate.model.ccd.raw.Document will;

    public Document findWill(CallbackRequest callbackRequest){
        listOfUploadedWills = new ArrayList<UploadDocument>();
        listOfScannedWills = new ArrayList<ScannedDocument>();
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if(caseData.getBoDocumentsUploaded() != null){
            for(CollectionMember<UploadDocument> document : caseData.getBoDocumentsUploaded()){
                if(document.getValue().getDocumentType() == DocumentType.WILL){
                    listOfUploadedWills.add(document.getValue());
                }
            }
        }
        if(caseData.getScannedDocuments() != null) {
            for (CollectionMember<ScannedDocument> document : caseData.getScannedDocuments()) {
                if (document.getValue().getSubtype() == "will") {
                    listOfScannedWills.add(document.getValue());
                }
            }
        }

        if((listOfUploadedWills.size() + listOfScannedWills.size()) == 1){
            if(!listOfUploadedWills.isEmpty()){
                will = pdfManagementService.generateAndUpload(toJson(listOfUploadedWills.get(0)), DocumentType.WILL);
            }
            else {
                will = pdfManagementService
                        .generateAndUpload(toJson(listOfScannedWills.get(0)), DocumentType.WILL);
            }
        }
        return will;
    }

    private String toJson(Object data) {
        objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
