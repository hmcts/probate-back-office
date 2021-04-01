package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplying;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplyingReason;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OCRFieldAdditionalExecutorsNotApplyingMapperTest {

    private static final String EXECUTOR_NOT_APPLYING_1_NAME = "Conner O'Mailey";
    private static final String EXECUTOR_NOT_APPLYING_1_REASON = "A";
    private static final String EXECUTOR_NOT_APPLYING_2_NAME = "Tom Smith";
    private static final String EXECUTOR_NOT_APPLYING_2_REASON = "b";
    private static final String EXECUTOR_NOT_APPLYING_3_NAME = "Bob Hoskins";
    private static final String EXECUTOR_NOT_APPLYING_3_REASON = "F";

    private OCRFieldAdditionalExecutorsNotApplyingMapper ocrFieldAdditionalExecutorsNotApplyingMapper
        = new OCRFieldAdditionalExecutorsNotApplyingMapper();

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFields2;
    private ExceptionRecordOCRFields ocrFields3;
    private ExceptionRecordOCRFields ocrFields4;
    private ExceptionRecordOCRFields ocrFields5;
    private ExceptionRecordOCRFields ocrFields6;

    @Before
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
            .executorsNotApplying0notApplyingExecutorName(EXECUTOR_NOT_APPLYING_1_NAME)
            .executorsNotApplying0notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_1_REASON)
            .build();

        ocrFields2 = ExceptionRecordOCRFields.builder()
            .executorsNotApplying0notApplyingExecutorName(EXECUTOR_NOT_APPLYING_1_NAME)
            .executorsNotApplying0notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_1_REASON)
            .executorsNotApplying1notApplyingExecutorName(EXECUTOR_NOT_APPLYING_2_NAME)
            .executorsNotApplying1notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_2_REASON)
            .executorsNotApplying2notApplyingExecutorName(EXECUTOR_NOT_APPLYING_3_NAME)
            .executorsNotApplying2notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_3_REASON)
            .build();

        ocrFields3 = ExceptionRecordOCRFields.builder()
            .build();

        ocrFields4 = ExceptionRecordOCRFields.builder()
            .executorsNotApplying0notApplyingExecutorName(null)
            .executorsNotApplying0notApplyingExecutorReason(null)
            .build();

        ocrFields5 = ExceptionRecordOCRFields.builder()
            .executorsNotApplying0notApplyingExecutorName(EXECUTOR_NOT_APPLYING_1_NAME)
            .executorsNotApplying0notApplyingExecutorReason(null)
            .build();

        ocrFields6 = ExceptionRecordOCRFields.builder()
            .executorsNotApplying0notApplyingExecutorName(EXECUTOR_NOT_APPLYING_1_NAME)
            .executorsNotApplying0notApplyingExecutorReason("QQ")
            .build();
    }

    @Test
    public void testExecutorsNotApplying() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields);
        assertEquals(EXECUTOR_NOT_APPLYING_1_NAME, response.get(0).getValue().getNotApplyingExecutorName());
        assertEquals(ExecutorNotApplyingReason.DIED_BEFORE, response.get(0).getValue().getNotApplyingExecutorReason());
        assertTrue(response.get(0).getValue().getNotApplyingExecutorDiedBefore());
        assertFalse(response.get(0).getValue().getNotApplyingExecutorIsDead());
        assertEquals(1, response.size());
    }

    @Test
    public void testMultipleExecutorsNotApplying() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields2);
        assertEquals(EXECUTOR_NOT_APPLYING_1_NAME, response.get(0).getValue().getNotApplyingExecutorName());
        assertEquals(ExecutorNotApplyingReason.DIED_BEFORE, response.get(0).getValue().getNotApplyingExecutorReason());
        assertTrue(response.get(0).getValue().getNotApplyingExecutorDiedBefore());
        assertFalse(response.get(0).getValue().getNotApplyingExecutorIsDead());
        assertEquals(EXECUTOR_NOT_APPLYING_2_NAME, response.get(1).getValue().getNotApplyingExecutorName());
        assertEquals(ExecutorNotApplyingReason.DIED_AFTER, response.get(1).getValue().getNotApplyingExecutorReason());
        assertFalse(response.get(1).getValue().getNotApplyingExecutorDiedBefore());
        assertTrue(response.get(1).getValue().getNotApplyingExecutorIsDead());
        assertEquals(EXECUTOR_NOT_APPLYING_3_NAME, response.get(2).getValue().getNotApplyingExecutorName());
        assertEquals(ExecutorNotApplyingReason.MENTALLY_INCAPABLE,
            response.get(2).getValue().getNotApplyingExecutorReason());
        assertFalse(response.get(2).getValue().getNotApplyingExecutorDiedBefore());
        assertFalse(response.get(2).getValue().getNotApplyingExecutorIsDead());
        assertEquals(3, response.size());
    }

    @Test
    public void testNoExecutorNotApplying() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields3);
        assertEquals(0, response.size());
    }

    @Test
    public void testNullExecutorNotApplyingShouldReturnNoRow() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields4);
        assertEquals(0, response.size());
    }

    @Test(expected = OCRMappingException.class)
    public void testMissingReasonExecutorNotApplyingShouldError() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields5);
    }

    @Test(expected = OCRMappingException.class)
    public void testInvalidReasonExecutorNotApplyingShouldError() {
        List<CollectionMember<ExecutorNotApplying>> response
            = ocrFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember(ocrFields6);
    }
}
