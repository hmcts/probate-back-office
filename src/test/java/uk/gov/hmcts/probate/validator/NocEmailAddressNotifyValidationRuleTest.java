package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

class NocEmailAddressNotifyValidationRuleTest {

    @InjectMocks
    private NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private CaseData caseData;
    private FieldErrorResponse fieldErrorResponseSolicitor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);


        fieldErrorResponseSolicitor = FieldErrorResponse.builder()
            .message("solicitor missing")
            .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailSOLS"))
            .thenReturn(fieldErrorResponseSolicitor);
    }

    @Test
    void shouldPassSolicitorWithEmail() {
        CollectionMember<ChangeOfRepresentative> representative =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder().removedRepresentative(RemovedRepresentative.builder()
                                .solicitorEmail("solicitor@gmail.com").build())
                        .build());
        caseData = CaseData.builder()
            .applicationType(SOLICITOR)
            .changeOfRepresentatives(Arrays.asList(representative))
            .build();
        List<FieldErrorResponse> validationErrors = nocEmailAddressNotifyValidationRule.validate(caseData);

        assertTrue(validationErrors.isEmpty());
        verify(businessValidationMessageService, times(0)).generateError(any(String.class),
                any(String.class));
    }

    @Test
    void shouldFailSolicitorWithNoEmail() {
        caseData = CaseData.builder()
            .applicationType(SOLICITOR)
            .build();
        List<FieldErrorResponse> validationErrors = nocEmailAddressNotifyValidationRule.validate(caseData);

        assertTrue(validationErrors.size() == 1);
        assertEquals(validationErrors.get(0).getMessage(), "solicitor missing");
    }

}
