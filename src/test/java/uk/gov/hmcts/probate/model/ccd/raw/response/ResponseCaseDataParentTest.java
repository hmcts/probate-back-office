package uk.gov.hmcts.probate.model.ccd.raw.response;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;

import static org.junit.Assert.assertEquals;

public class ResponseCaseDataParentTest {

    @Test
    public void shouldApplyParentAttributes() {
        DynamicList reprintDocument = DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect = DynamicList.builder().value(DynamicListItem.builder().code("solsAmendLegalStatmentSelect").build()).build();

        final ResponseCaseDataParent responseCaseDataParent = ResponseCaseDataParent.builder()
            .reprintDocument(reprintDocument).reprintNumberOfCopies("1").solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
            .declarationCheckbox("Yes")
            .ihtGrossValueField("1000").ihtNetValueField("900")
            .numberOfExecutors(1L).numberOfApplicants(2L)
            .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
            .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress").registrySequenceNumber("registrySequenceNumber")
            .build();

        assertEquals("reprintDocument", responseCaseDataParent.getReprintDocument().getValue().getCode());
        assertEquals("1", responseCaseDataParent.getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect", responseCaseDataParent.getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("Yes", responseCaseDataParent.getDeclarationCheckbox());
        assertEquals("1000", responseCaseDataParent.getIhtGrossValueField());
        assertEquals("900", responseCaseDataParent.getIhtNetValueField());
        assertEquals(Long.valueOf(1), responseCaseDataParent.getNumberOfExecutors());
        assertEquals(Long.valueOf(2), responseCaseDataParent.getNumberOfApplicants());
        assertEquals("legalDeclarationJson", responseCaseDataParent.getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", responseCaseDataParent.getCheckAnswersSummaryJson());
        assertEquals("registryAddress", responseCaseDataParent.getRegistryAddress());
        assertEquals("registryEmailAddress", responseCaseDataParent.getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", responseCaseDataParent.getRegistrySequenceNumber());
    }


}