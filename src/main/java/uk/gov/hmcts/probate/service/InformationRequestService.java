package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFGeneratorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestService {

    private final GenericMapperService gms;
    private final PDFGeneratorService pdfGeneratorService;

    public byte[] sotInformationRequest(CaseDetails caseDetails, DocumentType documentType) {
        return pdfGeneratorService.generateDocmosisHtml(documentType.getTemplateName(),
                gms.addCaseDataWithRegistryProperties(caseDetails));
    }
}
