package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_CREATED;

@Service
@Slf4j
@AllArgsConstructor
public class DefaultDocumentUploadTypesTransformer {
    
    /*
      {"LiveFrom": "01/01/2017", "ID": "documentUploadTypeEnum", "ListElementCode": "email", "ListElement": "Email"},
  {"LiveFrom": "01/01/2017", "ID": "documentUploadTypeEnum", "ListElementCode": "correspondence", "ListElement": "Correspondence"},
  {"LiveFrom": "01/01/2017", "ID": "documentUploadTypeEnum", "ListElementCode": "IHT", "ListElement": "Codicil"},
  {"LiveFrom": "01/01/2017", "ID": "documentUploadTypeEnum", "ListElementCode": "deathCertificate", "ListElement": "Death Certificate"},
  {"LiveFrom": "01/01/2017", "ID": "documentUploadTypeEnum", "ListElementCode": "other", "ListElement": "Other"},

     */
    public static final String DOCUMENT_UPLOAD_TYPE_WILL = "Will";
    public static final String DOCUMENT_UPLOAD_TYPE_WILL_CODE = "will";
    public static final String DOCUMENT_UPLOAD_TYPE_EMAIL = "Email";
    public static final String DOCUMENT_UPLOAD_TYPE_EMAIL_CODE = "email";
    public static final String DOCUMENT_UPLOAD_TYPE_CORRESPONDENCE = "Correspondence";
    public static final String DOCUMENT_UPLOAD_TYPE_CORRESPONDENCE_CODE = "correspondence";
    public static final String DOCUMENT_UPLOAD_TYPE_CODICIL = "Codicil";
    public static final String DOCUMENT_UPLOAD_TYPE_CODICIL_CODE = "IHT";
    public static final String DOCUMENT_UPLOAD_TYPE_DEATH_CERTIFICATE = "Death Certificate";
    public static final String DOCUMENT_UPLOAD_TYPE_DEATH_CERTIFICATE_CODE = "deathCertificate";
    public static final String DOCUMENT_UPLOAD_TYPE_OTHER = "Other";
    public static final String DOCUMENT_UPLOAD_TYPE_OTHER_CODE = "other";

    public void transformDocumentUploadTypes(@Valid CaseDetails caseDetails,
                                          ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        boolean isIntestacy = GrantType.INTESTACY.name().equals(caseDetails.getData().getCaseType());
        for (CollectionMember<UploadDocument> collectionMember :caseDetails.getData().getBoDocumentsUploaded()) {
            List<DynamicListItem> listItems = new ArrayList<>();
            if (isIntestacy) {
                listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_WILL_CODE, DOCUMENT_UPLOAD_TYPE_WILL));
            }
            listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_EMAIL_CODE, DOCUMENT_UPLOAD_TYPE_EMAIL));
            listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_CORRESPONDENCE_CODE, DOCUMENT_UPLOAD_TYPE_CORRESPONDENCE));
            listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_CODICIL_CODE, DOCUMENT_UPLOAD_TYPE_CODICIL));
            listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_DEATH_CERTIFICATE_CODE, DOCUMENT_UPLOAD_TYPE_DEATH_CERTIFICATE));
            listItems.add(buildListItem(DOCUMENT_UPLOAD_TYPE_OTHER_CODE, DOCUMENT_UPLOAD_TYPE_OTHER));
            collectionMember.getValue().getDocumentType().setListItems(listItems);
        }
        responseCaseDataBuilder.boDocumentsUploaded(caseDetails.getData().getBoDocumentsUploaded());
    }

    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

}
