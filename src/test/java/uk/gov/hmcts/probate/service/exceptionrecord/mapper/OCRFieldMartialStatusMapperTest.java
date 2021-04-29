package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Component
public class OCRFieldMartialStatusMapperTest {

    private static final String OCR_FORM_NEVER_MARRIED = "neverMarried";
    private static final String OCR_FORM_WIDOWED = "widowed";
    private static final String OCR_FORM_MARRIED_CIVIL_PARTNERSHIP = "marriedCivilPartnership";
    private static final String OCR_FORM_DIVORCED_CIVIL_PARTNERSHIP = "divorcedCivilPartnership";
    private static final String OCR_FORM_JUDICIALLY = "judicially";

    private OCRFieldMartialStatusMapper martialStatusMapper = new OCRFieldMartialStatusMapper();

    @Test
    public void testMartialStatusNeverMarried() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(OCR_FORM_NEVER_MARRIED);
        assertEquals(MaritalStatus.NEVER_MARRIED, response);
    }

    @Test
    public void testMartialStatusWidowed() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(OCR_FORM_WIDOWED);
        assertEquals(MaritalStatus.WIDOWED, response);
    }

    @Test
    public void testMartialStatusMarriedCivilPartnership() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(OCR_FORM_MARRIED_CIVIL_PARTNERSHIP);
        assertEquals(MaritalStatus.MARRIED, response);
    }

    @Test
    public void testMartialStatusDivorcedCivilPartnership() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(OCR_FORM_DIVORCED_CIVIL_PARTNERSHIP);
        assertEquals(MaritalStatus.DIVORCED, response);
    }

    @Test
    public void testMartialStatusJudicial() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(OCR_FORM_JUDICIALLY);
        assertEquals(MaritalStatus.JUDICIALLY_SEPARATED, response);
    }

    @Test
    public void testMartialStatusNull() {
        MaritalStatus response = martialStatusMapper.toMartialStatus(null);
        assertNull(response);
    }

    @Test
    public void testMartialStatusEmpty() {
        MaritalStatus response = martialStatusMapper.toMartialStatus("");
        assertNull(response);
    }

    @Test(expected = OCRMappingException.class)
    public void testMartialStatusError() {
        MaritalStatus response = martialStatusMapper.toMartialStatus("notfound");
        assertTrue(false);
    }
}