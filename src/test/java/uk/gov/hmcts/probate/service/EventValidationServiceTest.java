package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;

class EventValidationServiceTest {

    @InjectMocks
    private EventValidationService eventValidationService;

    @Mock
    private CCDData ccdDataMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private CaveatData caveatDataMock;
    @Mock
    private PaymentResponse paymentResponseMock;
    @Mock
    private NocEmailAddressNotifyValidationRule nocEmailAddressValidationRuleMock;

    private SimpleValidationRule validationRule;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        validationRule = new SimpleValidationRule();
    }


    @Test
    void shouldGatherValidationErrors() {

        List<FieldErrorResponse> fieldErrorResponses = eventValidationService
            .validate(ccdDataMock, Collections.singletonList(validationRule));

        assertEquals(2, fieldErrorResponses.size());

    }

    @Test
    void shouldGatherNocValidationErrors() {

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(),
                FieldErrorResponse.builder().build());
        caseDataMock = CaseData.builder()
                .applicationType(SOLICITOR)
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com").build())
                .build();
        when(nocEmailAddressValidationRuleMock.validate(caseDataMock.getApplicationType(),
                caseDataMock.getRemovedRepresentative().getSolicitorEmail())).thenReturn(errors);
        CallbackResponse fieldErrorResponses = eventValidationService
                .validateNocEmail(caseDataMock, nocEmailAddressValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

    }

    @Test
    void shouldGatherCaveatNocValidationErrors() {

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(),
                FieldErrorResponse.builder().build());
        caveatDataMock = CaveatData.builder()
                .applicationType(SOLICITOR)
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com").build())
                .build();
        when(nocEmailAddressValidationRuleMock.validate(caveatDataMock.getApplicationType(),
                caveatDataMock.getRemovedRepresentative().getSolicitorEmail())).thenReturn(errors);
        CaveatCallbackResponse fieldErrorResponses = eventValidationService
                .validateCaveatNocEmail(caveatDataMock, nocEmailAddressValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

    }

    @Test
    void testErrorSepBeforeBirth() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDateOfBirth())
                .thenReturn(LocalDate.of(1990, 1, 1));
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(LocalDate.of(2024, 10, 10));
        when(caseData.getDateOfDivorcedCPJudicially())
                .thenReturn(LocalDate.of(1989, 12, 25).toString());

        final List<String> actual = eventValidationService.generateErrorsSepDateBounds(caseData);

        assertAll(
                () -> assertThat(actual, hasSize(2)),
                () -> assertThat(actual, hasItem(EventValidationService.SEP_DATE_BEFORE_DOB_EN)),
                () -> assertThat(actual, hasItem(EventValidationService.SEP_DATE_BEFORE_DOB_CY))
        );
    }

    @Test
    void testErrorSepInRange() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDateOfBirth())
                .thenReturn(LocalDate.of(1990, 1, 1));
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(LocalDate.of(2024, 10, 10));
        when(caseData.getDateOfDivorcedCPJudicially())
                .thenReturn(LocalDate.of(2022, 12, 25).toString());

        final List<String> actual = eventValidationService.generateErrorsSepDateBounds(caseData);

        assertThat(actual, empty());
    }

    @Test
    void testErrorSepEmptyString() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDateOfBirth())
                .thenReturn(LocalDate.of(1990, 1, 1));
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(LocalDate.of(2024, 10, 10));
        when(caseData.getDateOfDivorcedCPJudicially())
                .thenReturn("");

        final List<String> actual = eventValidationService.generateErrorsSepDateBounds(caseData);

        assertThat(actual, empty());
    }

    @Test
    void testErrorSepUnset() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDateOfBirth())
                .thenReturn(LocalDate.of(1990, 1, 1));
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(LocalDate.of(2024, 10, 10));
        when(caseData.getDateOfDivorcedCPJudicially())
                .thenReturn(null);

        final List<String> actual = eventValidationService.generateErrorsSepDateBounds(caseData);

        assertThat(actual, empty());
    }

    @Test
    void testErrorSepAfterDeath() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDateOfBirth())
                .thenReturn(LocalDate.of(1990, 1, 1));
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(LocalDate.of(2024, 10, 10));
        when(caseData.getDateOfDivorcedCPJudicially())
                .thenReturn(LocalDate.of(2025, 11, 11).toString());

        final List<String> actual = eventValidationService.generateErrorsSepDateBounds(caseData);

        assertAll(
                () -> assertThat(actual, hasSize(2)),
                () -> assertThat(actual, hasItem(EventValidationService.SEP_DATE_AFTER_DOD_EN)),
                () -> assertThat(actual, hasItem(EventValidationService.SEP_DATE_AFTER_DOD_CY))
        );
    }

    @Test
    void testErrorSepOutsideDivCivil() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDivorcedInEnglandOrWales())
                .thenReturn("No");
        when(caseData.getDeceasedMaritalStatus())
                .thenReturn("divorcedCivilPartnership");

        final List<String> actual = eventValidationService.generateErrorsSepOutsideEngWales(caseData);

        assertAll(
                () -> assertThat(actual, hasSize(2)),
                () -> assertThat(actual, hasItem(EventValidationService.DIV_DISS_OUTSIDE_ENG_WALES_EN)),
                () -> assertThat(actual, hasItem(EventValidationService.DIV_DISS_OUTSIDE_ENG_WALES_CY))
        );
    }

    @Test
    void testErrorSepOutsideJudicially() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDivorcedInEnglandOrWales())
                .thenReturn("No");
        when(caseData.getDeceasedMaritalStatus())
                .thenReturn("judicially");

        final List<String> actual = eventValidationService.generateErrorsSepOutsideEngWales(caseData);

        assertAll(
                () -> assertThat(actual, hasSize(2)),
                () -> assertThat(actual, hasItem(EventValidationService.SEPARATION_OUTSIDE_ENG_WALES_EN)),
                () -> assertThat(actual, hasItem(EventValidationService.SEPARATION_OUTSIDE_ENG_WALES_CY))
        );
    }

    @Test
    void testErrorSepOutsideOther() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDivorcedInEnglandOrWales())
                .thenReturn("No");
        when(caseData.getDeceasedMaritalStatus())
                .thenReturn("other");

        final List<String> actual = eventValidationService.generateErrorsSepOutsideEngWales(caseData);

        assertThat(actual, empty());
    }

    @Test
    void testErrorSepInsideDivCivil() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDivorcedInEnglandOrWales())
                .thenReturn("Yes");
        when(caseData.getDeceasedMaritalStatus())
                .thenReturn("divorcedCivilPartnership");

        final List<String> actual = eventValidationService.generateErrorsSepOutsideEngWales(caseData);

        assertThat(actual, empty());
    }

    @Test
    void testErrorSepInsideJudicially() {
        final CaseData caseData = mock();

        when(caseData.getDeceasedDivorcedInEnglandOrWales())
                .thenReturn("Yes");
        when(caseData.getDeceasedMaritalStatus())
                .thenReturn("judicially");

        final List<String> actual = eventValidationService.generateErrorsSepOutsideEngWales(caseData);

        assertThat(actual, empty());
    }

    private class SimpleValidationRule implements ValidationRule {
        private FieldErrorResponse fieldErrorResponse1Mock;

        private FieldErrorResponse fieldErrorResponse2Mock;

        @Override
        public List<FieldErrorResponse> validate(CCDData form) {
            return Arrays.asList(fieldErrorResponse1Mock, fieldErrorResponse2Mock);
        }
    }
}
