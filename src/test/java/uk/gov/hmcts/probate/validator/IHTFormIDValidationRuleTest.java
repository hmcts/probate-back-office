package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT205_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;

class IHTFormIDValidationRuleTest {

    @InjectMocks
    private IHTFormIDValidationRule ihtFormIDValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataMock = CaseData.builder()
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate(IHT400_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldReturnErrorForNaSelection() {
        dataMock = CaseData.builder()
                .ihtFormId(NOT_APPLICABLE_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            ihtFormIDValidationRule.validate(detailsMock);
        });
        assertEquals("IHTFormID is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnErrorForIHT400421Selection() {
        dataMock = CaseData.builder()
                .ihtFormId(IHT400421_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            ihtFormIDValidationRule.validate(detailsMock);
        });
        assertEquals("IHTFormID is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnNoerrorForIHT400() {
        dataMock = CaseData.builder()
                .ihtFormId(IHT400_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        ihtFormIDValidationRule.validate(detailsMock);
    }

    @Test
    void shouldReturnNoerrorForIHT205() {
        dataMock = CaseData.builder()
                .ihtFormId(IHT205_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        ihtFormIDValidationRule.validate(detailsMock);
    }

    @Test
    void shouldReturnNoerrorForIHT207() {
        dataMock = CaseData.builder()
                .ihtFormId(IHT207_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        ihtFormIDValidationRule.validate(detailsMock);
    }
}
