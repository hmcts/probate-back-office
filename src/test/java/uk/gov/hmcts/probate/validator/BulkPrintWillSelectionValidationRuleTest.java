package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulkPrintWillSelectionValidationRuleTest {

    @InjectMocks
    private BulkPrintWillSelectionValidationRule bulkPrintWillSelectionValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;


    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test(expected = BusinessValidationException.class)
    public void shouldThrowExceptionWhenNoWillSelectionMade() {
        CollectionMember<WillDocument> will1 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList()).build());
        CollectionMember<WillDocument> will2 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList()).build());
        CollectionMember<WillDocument> will3 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList("No")).build());
        List<CollectionMember<WillDocument>> willSelection = Arrays.asList(will1, will2, will3);
        when(caseDataMock.getWillSelection()).thenReturn(willSelection);
        bulkPrintWillSelectionValidationRule.validate(caseDetailsMock);

        verify(businessValidationMessageRetriever, times(1)).getMessage(any(), any(), any());
    }

    @Test
    public void shouldNotThrowExceptionWhenWillSelectionMade() {
        CollectionMember<WillDocument> will1 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList("Yes")).build());
        CollectionMember<WillDocument> will2 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList()).build());
        CollectionMember<WillDocument> will3 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList("No")).build());
        CollectionMember<WillDocument> will4 =
            new CollectionMember<>(WillDocument.builder().documentSelected(Arrays.asList("Yes")).build());
        List<CollectionMember<WillDocument>> willSelection = Arrays.asList(will1, will2, will3, will4);
        when(caseDataMock.getWillSelection()).thenReturn(willSelection);

        bulkPrintWillSelectionValidationRule.validate(caseDetailsMock);

        verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
    }
}