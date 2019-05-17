package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
public class ExcelaCriteriaServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private ImmutableList.Builder<ReturnedCaseDetails> cases = new ImmutableList.Builder<>();
    private ReturnedCaseDetails case1;
    private ReturnedCaseDetails case2;
    private ReturnedCaseDetails case3;
    private ReturnedCaseDetails case4;

    private ExcelaCriteriaService excelaCriteriaService = new ExcelaCriteriaService();

    @Before
    public void setup() {
        final CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(new ScannedDocument("1",
                "test", "other", "will", LocalDateTime.now(), DocumentLink.builder().build(),
                "test", LocalDateTime.now()));
        final CollectionMember<ScannedDocument> scannedDocumentBoundary = new CollectionMember<>(new ScannedDocument(
                "2",
                "test", "other", "will", LocalDateTime.of(2019, 04, 01, 00, 00), DocumentLink.builder().build(),
                "test", LocalDateTime.now()));
        final CollectionMember<ScannedDocument> scannedDocumentBelowBoundary =
                new CollectionMember<>(new ScannedDocument("3",
                        "test", "other", "will",
                        LocalDateTime.of(2019, 03, 31, 23, 59),
                        DocumentLink.builder().build(),
                        "test", LocalDateTime.now()));
        final CollectionMember<ScannedDocument> scannedDocumentNullSubType = new CollectionMember<>(new ScannedDocument(
                "4",
                "test", "other", "", LocalDateTime.now(), DocumentLink.builder().build(),
                "test", LocalDateTime.now()));

        final CollectionMember<ScannedDocument> scannedDocumentCherished = new CollectionMember<>(new ScannedDocument(
                "5",
                "test", "Cherished", null, LocalDateTime.now(), DocumentLink.builder().build(),
                "test", LocalDateTime.now()));

        List<CollectionMember<ScannedDocument>> scannedDocumentsCase1 = new ArrayList<>();
        scannedDocumentsCase1.add(scannedDocument);
        scannedDocumentsCase1.add(scannedDocumentNullSubType);

        List<CollectionMember<ScannedDocument>> scannedDocumentsCase2 = new ArrayList<>();
        scannedDocumentsCase2.add(scannedDocumentBoundary);
        scannedDocumentsCase2.add(scannedDocumentBelowBoundary);

        List<CollectionMember<ScannedDocument>> scannedDocumentsCase3 = new ArrayList<>();
        scannedDocumentsCase3.add(scannedDocumentBelowBoundary);
        scannedDocumentsCase3.add(scannedDocumentNullSubType);

        List<CollectionMember<ScannedDocument>> scannedDocumentsCase4 = new ArrayList<>();
        scannedDocumentsCase4.add(scannedDocumentNullSubType);
        scannedDocumentsCase4.add(scannedDocumentCherished);
        scannedDocumentsCase4.add(scannedDocument);

        CaseData caseData1 = CaseData.builder()
                .scannedDocuments(scannedDocumentsCase1)
                .deceasedSurname("Smith")
                .build();

        CaseData caseData2 = CaseData.builder()
                .scannedDocuments(scannedDocumentsCase2)
                .deceasedSurname("Johnson")
                .build();

        CaseData caseData3 = CaseData.builder()
                .scannedDocuments(scannedDocumentsCase3)
                .deceasedSurname("Wrongun")
                .build();

        CaseData caseData4 = CaseData.builder()
                .scannedDocuments(scannedDocumentsCase4)
                .deceasedSurname("Orderson")
                .build();

        case1 = new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L);
        case2 = new ReturnedCaseDetails(caseData2, LAST_MODIFIED, 2L);
        case3 = new ReturnedCaseDetails(caseData3, LAST_MODIFIED, 3L);
        case4 = new ReturnedCaseDetails(caseData4, LAST_MODIFIED, 3L);
    }

    @Test
    public void testValidCaseShouldBeReturned() {
        cases.add(case1);
        assertThat(excelaCriteriaService.getFilteredCases(cases.build()).size(), is(1));
    }

    @Test
    public void testBoundaryCaseShouldBeReturned() {
        cases.add(case2);
        assertThat(excelaCriteriaService.getFilteredCases(cases.build()).size(), is(1));
    }

    @Test
    public void testInvalidDocumentsShouldNotReturnCase() {
        cases.add(case3);
        assertThat(excelaCriteriaService.getFilteredCases(cases.build()).size(), is(0));
    }

    @Test
    public void testEmptySubtypePrecedingValidSubtypeShouldReturnCase() {
        cases.add(case4);
        assertThat(excelaCriteriaService.getFilteredCases(cases.build()).size(), is(1));
    }
}
