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

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.service.ReprintService.LABEL_GRANT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_REISSUED_GRANT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_SOT;
import static uk.gov.hmcts.probate.service.ReprintService.LABEL_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;

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
                .toList());
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
        return ((DOC_TYPE_OTHER.equalsIgnoreCase(document.getType())
                && DOC_SUBTYPE_WILL.equalsIgnoreCase(document.getSubtype()))
                || DOC_TYPE_WILL.equalsIgnoreCase(document.getType()));
    }

    private boolean isFromGeneratedDocuments(Document document) {
        return switch (document.getDocumentType()) {
            case DIGITAL_GRANT, INTESTACY_GRANT, ADMON_WILL_GRANT, AD_COLLIGENDA_BONA_GRANT, WELSH_DIGITAL_GRANT,
                    WELSH_INTESTACY_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_AD_COLLIGENDA_BONA_GRANT ->
                    true;
            case DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE,
                    AD_COLLIGENDA_BONA_GRANT_REISSUE ->
                    true;
            case STATEMENT_OF_TRUTH, WELSH_STATEMENT_OF_TRUTH -> true;
            default -> false;
        };
    }

    private Optional<DynamicListItem> buildFromScannedDocument(ScannedDocument document) {
        return Optional.of(buildListItem(document.getFileName(), LABEL_WILL));
    }

    private Optional<DynamicListItem> buildFromGeneratedDocument(Document document) {
        Optional<DynamicListItem> optionalDynamicListItem = Optional.empty();
        switch (document.getDocumentType()) {
            case DIGITAL_GRANT, INTESTACY_GRANT, ADMON_WILL_GRANT, AD_COLLIGENDA_BONA_GRANT, WELSH_DIGITAL_GRANT,
                    WELSH_INTESTACY_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_AD_COLLIGENDA_BONA_GRANT:
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_GRANT));
                break;
            case DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE,
                    AD_COLLIGENDA_BONA_GRANT_REISSUE:
                optionalDynamicListItem =
                    Optional.of(buildListItem(document.getDocumentFileName(), LABEL_REISSUED_GRANT));
                break;
            case STATEMENT_OF_TRUTH, WELSH_STATEMENT_OF_TRUTH:
                optionalDynamicListItem = Optional.of(buildListItem(document.getDocumentFileName(), LABEL_SOT));
                break;
            default:
        }

        return optionalDynamicListItem;
    }

}
