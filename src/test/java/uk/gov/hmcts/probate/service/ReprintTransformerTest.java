package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.transformer.ReprintTransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_EXTENDED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;
import static uk.gov.hmcts.probate.model.DocumentType.STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_STATEMENT_OF_TRUTH;

public class ReprintTransformerTest {
    @InjectMocks
    private ReprintTransformer reprintTransformer;

    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;

    private List<CollectionMember<Document>> generatedDocs = Collections.EMPTY_LIST;
    private List<CollectionMember<Document>> sotDocs = Collections.EMPTY_LIST;
    private List<CollectionMember<ScannedDocument>> scannedDocs = Collections.EMPTY_LIST;

    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        responseCaseDataBuilder = ResponseCaseData.builder();
        when(caseDetails.getData()).thenReturn(caseData);
    }

    @Test
    public void shouldCreateMultipleListItems() {
        Document grant = Document.builder()
            .documentType(DIGITAL_GRANT)
            .documentFileName("Grant1")
            .build();
        Document other = Document.builder()
            .documentType(OTHER)
            .documentFileName("Other1")
            .build();
        Document draft = Document.builder()
            .documentType(DIGITAL_GRANT_DRAFT)
            .documentFileName("GrantDraft1")
            .build();
        generatedDocs = Arrays.asList(new CollectionMember(null, grant),
            new CollectionMember(null, other),
            new CollectionMember(null, draft));
        when(caseData.getProbateDocumentsGenerated()).thenReturn(generatedDocs);

        ScannedDocument will = ScannedDocument.builder()
            .type("Other")
            .fileName("Will1")
            .subtype("will")
            .build();
        ScannedDocument otherSc = ScannedDocument.builder()
            .type("Other")
            .fileName("otherSc1")
            .subtype("otherSc")
            .build();
        scannedDocs = Arrays.asList(new CollectionMember(null, will), new CollectionMember(null, otherSc));
        when(caseData.getScannedDocuments()).thenReturn(scannedDocs);

        Document sot1 = Document.builder()
            .documentType(CAVEAT_EXTENDED)
            .documentFileName("NON-SOT1")
            .build();
        Document sot2 = Document.builder()
            .documentType(STATEMENT_OF_TRUTH)
            .documentFileName("SOT2")
            .build();
        Document sot3 = Document.builder()
            .documentType(WELSH_STATEMENT_OF_TRUTH)
            .documentFileName("WSOT3")
            .build();
        sotDocs = Arrays.asList(new CollectionMember(null, sot1),
            new CollectionMember(null, sot2),
            new CollectionMember(null, sot3));
        when(caseData.getProbateSotDocumentsGenerated()).thenReturn(sotDocs);

        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(3));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getCode(), is("Will1"));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getLabel(), is("Will"));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(1).getCode(), is("Grant1"));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(1).getLabel(), is("Grant"));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(2).getCode(), is("WSOT3"));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(2).getLabel(), is("SOT"));
    }

    @Test
    public void shouldCreateSingleGeneratedListItems() {
        createAndAssertGeneratedListItem(DIGITAL_GRANT, "Grant1", "Grant");
        createAndAssertGeneratedListItem(WELSH_DIGITAL_GRANT, "WGrant1", "Grant");

        createAndAssertGeneratedListItem(INTESTACY_GRANT, "IGrant1", "Grant");
        createAndAssertGeneratedListItem(WELSH_INTESTACY_GRANT, "WIGrant1", "Grant");

        createAndAssertGeneratedListItem(ADMON_WILL_GRANT, "AWGrant1", "Grant");
        createAndAssertGeneratedListItem(WELSH_ADMON_WILL_GRANT, "WAWGrant1", "Grant");

        createAndAssertGeneratedListItem(DIGITAL_GRANT_REISSUE, "GrantReissue1", "ReissuedGrant");
        createAndAssertGeneratedListItem(INTESTACY_GRANT_REISSUE, "IGrantReissue1", "ReissuedGrant");
        createAndAssertGeneratedListItem(ADMON_WILL_GRANT_REISSUE, "AWGrantReissue1", "ReissuedGrant");
    }

    @Test
    public void shouldCreateSingleScannedListItems() {
        createAndAssertScannedListItem("Other", "will", "Will1", "Will");
    }

    @Test
    public void shouldNotCreateForNullItems() {
        generatedDocs = null;
        scannedDocs = null;
        sotDocs = null;
        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(0));
    }

    @Test
    public void shouldNotCreateForEmptyScannedItems() {
        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(0));
    }

    @Test
    public void shouldNotCreateForEmptyGeneratedItems() {
        ScannedDocument doc = ScannedDocument.builder()
            .type("Extra")
            .fileName("scFileName")
            .subtype("subType")
            .build();
        scannedDocs = Arrays.asList(new CollectionMember(null, doc));
        generatedDocs = null;
        when(caseData.getScannedDocuments()).thenReturn(scannedDocs);
        when(caseData.getProbateDocumentsGenerated()).thenReturn(generatedDocs);

        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(0));
    }

    private void createAndAssertGeneratedListItem(DocumentType docType, String genFileName, String genLabel) {
        Document doc = Document.builder()
            .documentType(docType)
            .documentFileName(genFileName)
            .build();
        generatedDocs = Arrays.asList(new CollectionMember(null, doc));
        when(caseData.getProbateDocumentsGenerated()).thenReturn(generatedDocs);

        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(1));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getCode(), is(genFileName));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getLabel(), is(genLabel));
    }

    private void createAndAssertScannedListItem(String docType, String subType, String scFileName, String scLabel) {
        ScannedDocument doc = ScannedDocument.builder()
            .type(docType)
            .fileName(scFileName)
            .subtype(subType)
            .build();
        scannedDocs = Arrays.asList(new CollectionMember(null, doc));
        when(caseData.getScannedDocuments()).thenReturn(scannedDocs);

        reprintTransformer.transformReprintDocuments(caseDetails, responseCaseDataBuilder);
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().size(), is(1));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getCode(), is(scFileName));
        assertThat(responseCaseDataBuilder.build().getReprintDocument().getListItems().get(0).getLabel(), is(scLabel));
    }
}