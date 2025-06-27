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
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT205_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;

class IHTFormIDValidationRuleTest {

    @InjectMocks
    private IHTFormIDValidationRule ihtFormIDValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;


    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final LocalDate DOD_BEFORE_2022 = LocalDate.of(2021, 12, 31);
    private static final LocalDate DOD_AFTER_2022 = LocalDate.of(2022, 1, 1);
    private static final Long CASE_ID = 12345678987654321L;
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataMock = CaseData.builder()
                .deceasedDateOfDeath(DOD_BEFORE_2022)
                .ihtFormEstate(IHT400_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
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
    void shouldReturnNoErrorForIHT400421SelectionAfter2022() {
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        dataMock = CaseData.builder()
                .deceasedDateOfDeath(DOD_AFTER_2022)
                .ihtFormId(IHT400421_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
        ihtFormIDValidationRule.validate(detailsMock);

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
