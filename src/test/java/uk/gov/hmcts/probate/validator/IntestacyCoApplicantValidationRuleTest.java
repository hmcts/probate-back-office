package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicRadioList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicRadioListElement;
import uk.gov.hmcts.probate.model.ccd.raw.SolsApplicantFamilyDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.validator.IntestacyCoApplicantValidationRule.ADOPTED_OUT;
import static uk.gov.hmcts.probate.validator.IntestacyCoApplicantValidationRule.ADOPTED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.probate.validator.IntestacyCoApplicantValidationRule.PARENT_IS_NOT_DECEASED;

@ExtendWith(SpringExtension.class)
class IntestacyCoApplicantValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private List<Executor> executorMock;

    private IntestacyCoApplicantValidationRule underTest;

    private static Stream<String> relationship() {
        return Stream.of(CHILD, GRAND_CHILD, WHOLE_BLOOD_SIBLING, HALF_BLOOD_SIBLING,
                WHOLE_BLOOD_NIECE_OR_NEPHEW, HALF_BLOOD_NIECE_OR_NEPHEW);
    }

    @BeforeEach
    public void setUp() {
        underTest = new IntestacyCoApplicantValidationRule(businessValidationMessageService);
        when(ccdDataMock.getExecutors()).thenReturn(executorMock);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUTSIDE_ENGLAND_OR_WALES))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUTSIDE_ENGLAND_OR_WALES).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUT))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUT).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, PARENT_IS_NOT_DECEASED))
                .thenReturn(FieldErrorResponse.builder().code(PARENT_IS_NOT_DECEASED).build());

    }

    @Test
    void testValidateWithSuccessWhenCoApplicantIsNull() {
        when(ccdDataMock.getExecutors()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateSuccessIfCoApplicantAdoptedInEnglandOrWales(final String relationship) {
        List<Executor> executor = List.of(
                Executor.builder()
                        .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                                .coApplicantAdoptedIn(YES)
                                .coApplicantAdoptionInEnglandOrWales(YES)
                                .relationship(DynamicRadioList.builder()
                                        .value(DynamicRadioListElement.builder()
                                                .code(relationship)
                                                .label(relationship)
                                                .build())
                                        .build()
                                )
                                .build()
                        ).build());
        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateFailureIfCoApplicantAdoptedOutsideEnglandOrWales(final String relationship) {
        List<Executor> executor = List.of(
                Executor.builder()
                        .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                                .coApplicantAdoptedIn(YES)
                                .coApplicantAdoptionInEnglandOrWales(NO)
                                .relationship(DynamicRadioList.builder()
                                        .value(DynamicRadioListElement.builder()
                                                .code(relationship)
                                                .label(relationship)
                                                .build())
                                        .build()
                                )
                                .build()
                        ).build());
        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateSuccessIfCoApplicantIsNotAdoptedOut(final String relationship) {
        List<Executor> executor = List.of(
                Executor.builder()
                    .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                            .coApplicantAdoptedIn(NO)
                            .coApplicantAdoptedOut(NO)
                            .relationship(DynamicRadioList.builder()
                                    .value(DynamicRadioListElement.builder()
                                            .code(relationship)
                                            .label(relationship)
                                            .build())
                            .build()
                    )
                    .build()
            ).build());
        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateFailureIfCoApplicantIsAdoptedOut(final String relationship) {
        List<Executor> executor = List.of(
            Executor.builder()
                    .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                            .coApplicantAdoptedIn(NO)
                            .coApplicantAdoptedOut(YES)
                            .relationship(DynamicRadioList.builder()
                                    .value(DynamicRadioListElement.builder()
                                            .code(relationship)
                                            .label(relationship)
                                            .build())
                                    .build()
                            )
                            .build()
                    ).build());

        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUT, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfCoApplicantIsGrandchildAndTheirParentIsDeceased() {
        List<Executor> executor = List.of(
                Executor.builder()
                        .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                                .coApplicantAdoptedIn(NO)
                                .coApplicantAdoptedOut(NO)
                                .grandchildParentDieBeforeDeceased(YES)
                                .relationship(DynamicRadioList.builder()
                                        .value(DynamicRadioListElement.builder()
                                                .code(GRAND_CHILD)
                                                .label("Grandchild")
                                                .build())
                                        .build()
                                )
                                .build()
                        ).build());
        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfCoApplicantIsGrandchildAndTheirParentIsNotDeceased() {
        List<Executor> executor = List.of(
            Executor.builder()
                    .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                            .coApplicantAdoptedIn(NO)
                            .coApplicantAdoptedOut(NO)
                            .grandchildParentDieBeforeDeceased(NO)
                            .relationship(DynamicRadioList.builder()
                                    .value(DynamicRadioListElement.builder()
                                            .code(GRAND_CHILD)
                                            .label("Grandchild")
                                            .build())
                                    .build()
                            )
                            .build()
                    ).build());

        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(PARENT_IS_NOT_DECEASED, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateFailureIfCoApplicantIsWholeNieceOrNephewAndTheirParentIsNotDeceased() {
        List<Executor> executor = List.of(
                Executor.builder()
                        .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                                .coApplicantAdoptedIn(NO)
                                .coApplicantAdoptedOut(NO)
                                .wholeNieceOrNephewParentDieBeforeDeceased(NO)
                                .relationship(DynamicRadioList.builder()
                                        .value(DynamicRadioListElement.builder()
                                                .code(WHOLE_BLOOD_NIECE_OR_NEPHEW)
                                                .label("wholeBloodNieceOrNephew")
                                                .build())
                                        .build()
                                )
                                .build()
                        ).build());

        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(PARENT_IS_NOT_DECEASED, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateFailureIfCoApplicantIsHalfNieceOrNephewAndTheirParentIsNotDeceased() {
        List<Executor> executor = List.of(
                Executor.builder()
                        .applicantFamilyDetails(SolsApplicantFamilyDetails.builder()
                                .coApplicantAdoptedIn(NO)
                                .coApplicantAdoptedOut(NO)
                                .halfNieceOrNephewParentDieBeforeDeceased(NO)
                                .relationship(DynamicRadioList.builder()
                                        .value(DynamicRadioListElement.builder()
                                                .code(HALF_BLOOD_NIECE_OR_NEPHEW)
                                                .label("halfBloodNieceOrNephew")
                                                .build())
                                        .build()
                                )
                                .build()
                        ).build());

        when(ccdDataMock.getExecutors()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(PARENT_IS_NOT_DECEASED, validationErrors.getFirst().getCode());
    }
}
