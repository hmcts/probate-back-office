package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.service.ReprintService.LABEL_GRANT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_REISSUED_GRANT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_SOT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_WILL;
import static uk.gov.hmcts.probate.service.ReprintService.WILL_DOC_SUB_TYPE;
import static uk.gov.hmcts.probate.service.ReprintService.WILL_DOC_TYPE;

@Service
@Slf4j
@AllArgsConstructor
public class ReprintTransformer {


    public void transformReprintDocuments(@Valid CaseDetails caseDetails,
                                          ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {
        responseCaseDataBuilder.reprintDocument(getDocumentsAsDynamicList(caseDetails.getData()));
    }

    private DynamicList getDocumentsAsDynamicList(CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<>();
        if (caseData.getScannedDocuments() != null) {
            listItems = caseData.getScannedDocuments().stream()
                .filter(doc -> isFromScannedDOcuments(doc.getValue()))
                .map(doc -> buildFromScannedDocument(doc.getValue()).get())
                .collect(Collectors.toList());
        }

        if (caseData.getProbateDocumentsGenerated() != null) {
            listItems.addAll(caseData.getProbateDocumentsGenerated().stream()
                .filter(doc -> isFromGeneratedDocuments(doc.getValue()))
                .map(doc -> buildFromGeneratedDocument(doc.getValue()).get())
                .collect(Collectors.toList()));
        }

        if (caseData.getProbateSotDocumentsGenerated() != null && !caseData.getProbateSotDocumentsGenerated()
            .isEmpty()) {
            Document sot =
                caseData.getProbateSotDocumentsGenerated().get(caseData.getProbateSotDocumentsGenerated().size() - 1)
                    .getValue();
            if (isFromGeneratedDocuments(sot)) {
                Optional<DynamicListItem> dynamicListItem = buildFromGeneratedDocument(sot);
                if (dynamicListItem.isPresent()) {
                    listItems.add(dynamicListItem.get());
                }
            }
        }

        return DynamicList.builder()
            .listItems(listItems)
            .value(DynamicListItem.builder().build())
            .build();
    }

    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

    private boolean isFromScannedDOcuments(ScannedDocument document) {
        return (WILL_DOC_TYPE.equalsIgnoreCase(document.getType())
            && WILL_DOC_SUB_TYPE.equalsIgnoreCase(document.getSubtype()));
    }

    private boolean isFromGeneratedDocuments(Document document) {
        switch (document.getDocumentType()) {
            case DIGITAL_GRANT:
            case INTESTACY_GRANT:
            case ADMON_WILL_GRANT:
            case WELSH_DIGITAL_GRANT:
            case WELSH_INTESTACY_GRANT:
            case WELSH_ADMON_WILL_GRANT:
                return true;
            case DIGITAL_GRANT_REISSUE:
            case INTESTACY_GRANT_REISSUE:
            case ADMON_WILL_GRANT_REISSUE:
                return true;
            case STATEMENT_OF_TRUTH:
            case WELSH_STATEMENT_OF_TRUTH:
                return true;
            default:
                return false;
        }
    }

    private Optional<DynamicListItem> buildFromScannedDocument(ScannedDocument document) {
        return Optional.of(buildListItem(document.getFileName(), LABEL_WILL));
    }

    private Optional<DynamicListItem> buildFromGeneratedDocument(Document document) {
        Optional<DynamicListItem> optionalDynamicListItem = Optional.empty();
        switch (document.getDocumentType()) {
            case DIGITAL_GRANT:
            case INTESTACY_GRANT:
            case ADMON_WILL_GRANT:
            case WELSH_DIGITAL_GRANT:
            case WELSH_INTESTACY_GRANT:
            case WELSH_ADMON_WILL_GRANT:
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_GRANT));
                break;
            case DIGITAL_GRANT_REISSUE:
            case INTESTACY_GRANT_REISSUE:
            case ADMON_WILL_GRANT_REISSUE:
                optionalDynamicListItem =
                    Optional.of(buildListItem(document.getDocumentFileName(), LABEL_REISSUED_GRANT));
                break;
            case STATEMENT_OF_TRUTH:
            case WELSH_STATEMENT_OF_TRUTH:
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_SOT));
                break;
            default:
        }

        return optionalDynamicListItem;
    }

}
