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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ReprintTransformer {

    private static final String LABEL_GRANT = "Grant";
    private static final String LABEL_REISSUED_GRANT = "ReissuedGrant";
    private static final String LABEL_WILL = "Will";
    private static final String LABEL_SOT = "SOT";
    private static final String WILL_DOC_TYPE = "Other";
    private static final String WILL_DOC_SUB_TYPE = "Will";

    public void transformReprintDocuments(@Valid CaseDetails caseDetails,
                                          ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        responseCaseDataBuilder.reprintDocument(getDocumentsAsDynamicList(caseDetails.getData()));
    }

    private DynamicList getDocumentsAsDynamicList(CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<>();
        if (caseData.getScannedDocuments() != null) {
            listItems = caseData.getScannedDocuments().stream()
                .filter(doc -> isWill(doc.getValue()))
                .map(doc -> buildFromScannedDocument(doc.getValue()).get())
                .collect(Collectors.toList());
        }

        if (caseData.getProbateDocumentsGenerated() != null) {
            listItems.addAll(caseData.getProbateDocumentsGenerated().stream()
                .filter(doc -> isGrantOrReissueOrSOT(doc.getValue()))
                .map(doc -> buildFromGeneratedDocument(doc.getValue()).get())
                .collect(Collectors.toList()));
        }

        DynamicList dynamicList = DynamicList.builder()
            .listItems(listItems)
            .value(DynamicListItem.builder().build())
            .build();

        return dynamicList;
    }

    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

    private boolean isWill(ScannedDocument document) {
        return (WILL_DOC_TYPE.equalsIgnoreCase(document.getType()) &&
            WILL_DOC_SUB_TYPE.equalsIgnoreCase(document.getSubtype()));
    }

    private boolean isGrantOrReissueOrSOT(Document document) {
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
                return true;
            default:
                return false;
        }
    }

    private Optional<DynamicListItem> buildFromScannedDocument(ScannedDocument document) {
        Optional<DynamicListItem> optionalDynamicListItem = Optional.of(buildListItem(document.getFileName(), LABEL_WILL));

        return optionalDynamicListItem;
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
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_REISSUED_GRANT));
                break;
            case STATEMENT_OF_TRUTH:
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_SOT));
                break;
            default:
        }

        return optionalDynamicListItem;
    }

}
