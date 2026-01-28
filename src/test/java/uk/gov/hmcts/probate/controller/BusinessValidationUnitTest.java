package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.CaseEscalatedService;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.NotificationService.RegistrarEscalationException;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorApplicationCompletionTransformer;
import uk.gov.hmcts.probate.validator.AdColligendaBonaCaseTypeValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkerAmendAndCreateValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkersSolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.CheckIntestacyMaritalStatusRule;
import uk.gov.hmcts.probate.validator.CheckIntestacyOtherApplicantRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.ChangeToSameStateValidationRule;
import uk.gov.hmcts.probate.validator.CodicilDateValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.probate.validator.FurtherEvidenceForApplicationValidationRule;
import uk.gov.hmcts.probate.validator.IHTFormIDValidationRule;
import uk.gov.hmcts.probate.validator.IHTFourHundredDateValidationRule;
import uk.gov.hmcts.probate.validator.IHTValidationRule;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;
import uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule;
import uk.gov.hmcts.probate.validator.IntestacyCoApplicantValidationRule;
import uk.gov.hmcts.probate.validator.IntestacyDivorceOrSeparationValidationRule;
import uk.gov.hmcts.probate.validator.NaValidationRule;
import uk.gov.hmcts.probate.validator.NumberOfApplyingExecutorsValidationRule;
import uk.gov.hmcts.probate.validator.OriginalWillSignedDateValidationRule;
import uk.gov.hmcts.probate.validator.Pre1900DOBValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.StopReasonValidationRule;
import uk.gov.hmcts.probate.validator.TitleAndClearingPageValidationRule;
import uk.gov.hmcts.probate.validator.UniqueCodeValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.ZeroApplyingExecutorsValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.Constants.CASE_PRINTED_NAME;


class BusinessValidationUnitTest {

    private static final Optional<String> STATE_GRANT_TYPE_PROBATE = Optional.of("SolProbateCreated");
    private static final String PAPERFORM = "PaperForm";
    private static final String AUTH_TOKEN = "AuthToken";
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());
    private static final String UPLOAD_DOCUMENT_EVENT = "uploadDocumentsDormantCase";
    @Mock
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    @Mock
    private EventValidationService eventValidationServiceMock;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private Document documentMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private FieldError fieldErrorMock;
    @Mock
    private List<ValidationRule> validationRules;
    @Mock
    private List<CaseworkerAmendAndCreateValidationRule> caseworkerAmendAndCreateValidationRules;
    @Mock
    private List<CheckListAmendCaseValidationRule> checkListAmendCaseValidationRules;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    private CallbackResponse callbackResponseMock;
    @Mock
    private CaseDataTransformer caseDataTransformerMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private AfterSubmitCallbackResponse afterSubmitCallbackResponseMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    @Mock
    private RedeclarationSoTValidationRule redeclarationSoTValidationRuleMock;
    @Mock
    private NumberOfApplyingExecutorsValidationRule numberOfApplyingExecutorsValidationRuleMock;

    private FieldErrorResponse businessValidationErrorMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;
    @Mock
    private CaseStoppedService  caseStoppedServiceMock;
    @Mock
    private CaseEscalatedService caseEscalatedServiceMock;
    @Mock
    private IHTFourHundredDateValidationRule ihtFourHundredDateValidationRule;
    @Mock
    private IhtEstateValidationRule ihtEstateValidationRule;
    @Mock
    private IHTValidationRule ihtValidationRule;
    @Mock
    private CodicilDateValidationRule codicilDateValidationRuleMock;
    @Mock
    private OriginalWillSignedDateValidationRule originalWillSignedDateValidationRuleMock;
    @Mock
    private SolicitorApplicationCompletionTransformer solCompletionTransformer;
    @Mock
    private ResetCaseDataTransformer resetCdTransformer;
    @Mock
    private LegalStatementExecutorTransformer legalStatementExecutorTransformer;
    @Mock
    private List<TitleAndClearingPageValidationRule> allTitleAndClearingValidationRules;
    @Mock
    private SolicitorPostcodeValidationRule solicitorPostcodeValidationRule;
    @Mock
    private CaseworkersSolicitorPostcodeValidationRule caseworkersSolicitorPostcodeValidationRule;
    @Mock
    private AssignCaseAccessService assignCaseAccessService;
    @Mock
    private FurtherEvidenceForApplicationValidationRule furtherEvidenceForApplicationValidationRule;
    @Mock
    private ChangeToSameStateValidationRule changeToSameStateValidationRule;
    @Mock
    private HandOffLegacyTransformer handOffLegacyTransformer;
    @Mock
    private RegistrarDirectionService registrarDirectionServiceMock;
    @Mock
    private UniqueCodeValidationRule uniqueCodeValidationRule;
    @Mock
    private StopReasonValidationRule stopReasonValidationRule;
    @Mock
    private NaValidationRule naValidationRule;
    @Mock
    private IHTFormIDValidationRule ihtFormIDValidationRule;
    @Mock
    private IntestacyApplicantDetailsValidationRule intestacyApplicantDetailsValidationRule;
    @Mock
    private IntestacyCoApplicantValidationRule intestacyCoApplicantValidationRule;
    @Mock
    private IntestacyDivorceOrSeparationValidationRule intestacyDivorceOrSeparationValidationRule;
    @Mock
    private Pre1900DOBValidationRule pre1900DOBValidationRuleMock;
    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;
    @Mock
    private AdColligendaBonaCaseTypeValidationRule adColligendaBonaCaseTypeValidationRule;
    @Mock
    private CheckIntestacyMaritalStatusRule checkIntestacyMaritalStatusRule;
    @Mock
    private CheckIntestacyOtherApplicantRule checkIntestacyOtherApplicantRule;
    @Mock
    private UserInfoService userInfoServiceMock;
    @Mock
    private ZeroApplyingExecutorsValidationRule zeroApplyingExecutorsValidationRule;
    @Mock
    private DocumentTransformer documentTransformerMock;

    @Mock
    private CaseEscalatedService caseEscalatedService;
    private BusinessValidationController underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        businessValidationErrorMock = FieldErrorResponse.builder().build();
        underTest = new BusinessValidationController(eventValidationServiceMock,
            notificationService,
            objectMapper,
            validationRules,
            caseworkerAmendAndCreateValidationRules,
            callbackResponseTransformerMock,
            caseDataTransformerMock,
            confirmationResponseServiceMock,
            stateChangeServiceMock,
            pdfManagementServiceMock,
            redeclarationSoTValidationRuleMock,
            numberOfApplyingExecutorsValidationRuleMock,
            codicilDateValidationRuleMock,
            originalWillSignedDateValidationRuleMock,
            allTitleAndClearingValidationRules,
            caseStoppedServiceMock,
            caseEscalatedServiceMock,
            emailAddressNotifyApplicantValidationRule,
            ihtFourHundredDateValidationRule,
            ihtEstateValidationRule,
            ihtValidationRule,
            uniqueCodeValidationRule,
            stopReasonValidationRule,
            naValidationRule,
            ihtFormIDValidationRule,
            solicitorPostcodeValidationRule,
            caseworkersSolicitorPostcodeValidationRule,
            assignCaseAccessService,
            furtherEvidenceForApplicationValidationRule,
            changeToSameStateValidationRule,
            handOffLegacyTransformer,
            registrarDirectionServiceMock,
            pre1900DOBValidationRuleMock,
            adColligendaBonaCaseTypeValidationRule,
            zeroApplyingExecutorsValidationRule,
            checkIntestacyOtherApplicantRule,
            checkIntestacyMaritalStatusRule,
            intestacyApplicantDetailsValidationRule,
            intestacyDivorceOrSeparationValidationRule,
            intestacyCoApplicantValidationRule,
            businessValidationMessageServiceMock,
            userInfoServiceMock,
            documentTransformerMock);

        when(httpServletRequest.getRequestURI()).thenReturn("/test-uri");
        doReturn(CASEWORKER_USERINFO).when(userInfoServiceMock).getCaseworkerInfo();
    }

    @Test
    void shouldValidateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(caseDataMock.getHmrcLetterId()).thenReturn(YES);
        when(stateChangeServiceMock.getChangedStateForGrantType(caseDataMock)).thenReturn(STATE_GRANT_TYPE_PROBATE);
        when(callbackResponseTransformerMock
            .transformForDeceasedDetails(callbackRequestMock, STATE_GRANT_TYPE_PROBATE))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateWithNoErrorsWithNoHmrcCode() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
                .thenReturn(callbackResponseMock);
        when(caseDataMock.getHmrcLetterId()).thenReturn(NO);
        when(callbackResponseTransformerMock
                .transformCase(callbackRequestMock, Optional.empty())).thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldVerifySolsAccessWithNoErrors() {
        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);

        ResponseEntity<AfterSubmitCallbackResponse> response = underTest.solicitorAccess(AUTH_TOKEN,
                "GrantOfRepresentation", callbackRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldVerifySolsCreatedWithNoErrors() {
        when(callbackResponseTransformerMock.createSolsCase(callbackRequestMock, AUTH_TOKEN))
                .thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response = underTest.createSolsCaseWithOrganisation(AUTH_TOKEN,
                callbackRequestMock);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(callbackRequestMock.getCaseDetails().getData().getHmrcLetterId()).thenReturn(YES);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForGrantType(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformForDeceasedDetails(callbackRequestMock, changedState))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateProbateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForProbateUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock,
            "gop"))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateProbate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateProbateWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForProbateUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState,
                Optional.empty()))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateProbate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateIntestacyWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForIntestacyUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_INTESTACY))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock,
            "intestacy"))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateIntestacy(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateIntestacyWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForIntestacyUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState,
                Optional.empty()))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateIntestacy(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateAdmonWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForAdmonUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_ADMON))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock,
            "admonWill"))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateAdmon(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateAdmonWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForAdmonUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState,
                Optional.empty()))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateAdmon(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldValidateWithFieldErrors() {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
            when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

            ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
                    bindingResultMock, httpServletRequest);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            assertThat(response.getBody().getErrors().isEmpty(), is(false));
        });
    }

    @Test
    void shouldValidateWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors())
            .thenReturn((businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList())));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getHmrcLetterId()).thenReturn(YES);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    void shouldValidateHmrcNoWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
                .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors())
                .thenReturn((businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList())));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getHmrcLetterId()).thenReturn(NO);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    void shouldValidateAmendCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors()).thenReturn(Collections.emptyList());

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        when(callbackResponseTransformerMock.transform(callbackRequestMock, Optional.empty()))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }


    @Test
    void shouldValidateAmendCaseWithFieldErrors() {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
            when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

            ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
                bindingResultMock, httpServletRequest);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            assertThat(response.getBody().getErrors().isEmpty(), is(false));
        });
    }

    @Test
    void shouldValidateAmendCaseWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors())
            .thenReturn((businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList())));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    void shouldErrorForConfirmation() {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

            ResponseEntity<AfterSubmitCallbackResponse> response = underTest.stopWithConfirmation(callbackRequestMock,
                bindingResultMock);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
        });
    }

    @Test
    void shouldPassConfirmation() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(confirmationResponseServiceMock.getStopConfirmation(Mockito.any(CallbackRequest.class)))
            .thenReturn(afterSubmitCallbackResponseMock);

        ResponseEntity<AfterSubmitCallbackResponse> response = underTest.stopWithConfirmation(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformCaseWithFieldErrors() {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
            when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

            ResponseEntity<CallbackResponse> response = underTest.casePrinted(callbackRequestMock,
                    bindingResultMock);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            assertThat(response.getBody().getErrors().isEmpty(), is(false));
        });
    }

    @Test
    void shouldTransformCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackResponseTransformerMock.transformCase(callbackRequestMock, CASEWORKER_USERINFO))
            .thenReturn(callbackResponseMock);
        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);
        ResponseEntity<CallbackResponse> response = underTest.casePrinted(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    void shouldPaperFormWithFieldErrors() throws NotificationClientException {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
            when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

            ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
                    bindingResultMock);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            assertThat(response.getBody().getErrors().isEmpty(), is(false));
        });
    }

    @Test
    void shouldSubmitForPersonalWithEmail() throws NotificationClientException {
        String paperFormValue = "Any";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER)))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock, CASEWORKER_USERINFO))
            .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    void shouldSubmitForPersonalWithoutEmail() throws NotificationClientException {
        String paperFormValue = "Any";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class)))
            .thenReturn(Arrays.asList(FieldErrorResponse.builder().build()));
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
        verify(notificationService, times(0))
            .sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
    }

    @Test
    void shouldSubmitPaperFormNoForPersonal() throws NotificationClientException {
        String paperFormValue = "No";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER)))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock, CASEWORKER_USERINFO))
            .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    void shouldSubmitForSolicitorWithoutEmail() throws NotificationClientException {
        String paperFormValue = "YesNo";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class)))
            .thenReturn(Arrays.asList(FieldErrorResponse.builder().build()));
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        verify(notificationService, times(0))
            .sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    void shouldSubmitForSolicitorWithEmail() throws NotificationClientException {
        String paperFormValue = "YesNo";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        verify(notificationService, times(1))
            .sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    void shouldSubmitForSolicitorPaperFormNoWithEmail() throws NotificationClientException {
        String paperFormValue = "No";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
                bindingResultMock);

        verify(notificationService, times(1))
                .sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    void shouldValidateIHT400Date() {
        ResponseEntity<CallbackResponse> response = underTest.solsValidateIHT400Date(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(callbackResponseTransformerMock).transform(any(), any());
    }

    @Test
    void shouldDefaultIHT400421PageFlow() {
        ResponseEntity<CallbackResponse> response = underTest.defaultIht400DatePage(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(callbackResponseTransformerMock).defaultIht400421DatePageFlow(any());
    }

    @Test
    void shouldSetGrantStoppedDateAfterCaseFailQa() {
        ResponseEntity<CallbackResponse> response = underTest.caseFailQa(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldUpdateTaskListWithEmptyCaseworkerInfo() {
        CallbackResponse expectedResponse = mock(CallbackResponse.class);
        when(callbackResponseTransformerMock.updateTaskList(callbackRequestMock, Optional.empty()))
                .thenReturn(expectedResponse);

        ResponseEntity<CallbackResponse> response = underTest.caseFailQa(callbackRequestMock);
        assertEquals(expectedResponse, response.getBody());
        verify(callbackResponseTransformerMock).updateTaskList(callbackRequestMock, Optional.empty());
    }

    @Test
    void shouldDefaultPBAs() {
        ResponseEntity<CallbackResponse> response =
            underTest.defaultSolicitorNextStepsForPayment(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(1))
            .transformCaseForSolicitorPayment(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldDefaultIHT() {
        ResponseEntity<CallbackResponse> response =
            underTest.defaultIhtEstateFromDateOfDeath(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(1))
            .defaultIhtEstateFromDateOfDeath(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidateIHTEstateData() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =
            underTest.validateIhtEstateData(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(ihtEstateValidationRule, times(1)).validate(caseDetailsMock);
        verify(naValidationRule, times(1)).validate(caseDetailsMock);
        verify(callbackResponseTransformerMock).transformValuesPage(callbackRequestMock);
    }

    @Test
    void shouldValidateIHTEstateDataWithError() {
        List<String> errors = new ArrayList<>();
        errors.add("some error");
        when(callbackResponseMock.getErrors()).thenReturn(errors);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =
                underTest.validateIhtEstateData(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(0))
                .transformValuesPage(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidateSolPostCode() {
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =  underTest.validateSolsCreate(callbackRequestMock);
        verify(callbackResponseTransformerMock).transform(callbackRequestMock, Optional.empty());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidateSolPostCodeCaseworker() {
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =  underTest.validateSolsCreateDefaultIhtEstate(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(1))
                .defaultIhtEstateFromDateOfDeath(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidateMissingSolPostCode() {
        List<String> errors = new ArrayList<>();
        errors.add("some error");
        when(callbackResponseMock.getErrors()).thenReturn(errors);
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =  underTest.validateSolsCreate(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(0))
                .transform(callbackRequestMock, CASEWORKER_USERINFO);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidateMissingSolPostCodeCaseworker() {
        List<String> errors = new ArrayList<>();
        errors.add("some error");
        when(callbackResponseMock.getErrors()).thenReturn(errors);
        when(eventValidationServiceMock.validateRequest(any(), any())).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =  underTest.validateSolsCreateDefaultIhtEstate(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(0)).defaultIhtEstateFromDateOfDeath(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformCaseDataForEvidenceHandledPACreateCaseOK() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);
        ResponseEntity<CallbackResponse> response =  underTest.paCreate(callbackRequestMock, bindingResultMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caseDataTransformerMock).transformCaseDataForEvidenceHandled(callbackRequestMock);
        verify(caseDataTransformerMock).transformIhtFormCaseDataByDeceasedDOD(callbackRequestMock);
        verify(caseDataTransformerMock).setApplicationSubmittedDateForPA(caseDetailsMock);
    }

    @Test
    void shouldTransformCaseDataForEvidenceHandledCasePrinted() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackResponseTransformerMock.transformCase(callbackRequestMock, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);
        ResponseEntity<CallbackResponse> response = underTest.casePrinted(callbackRequestMock,
                bindingResultMock);

        verify(caseDataTransformerMock).transformCaseDataForEvidenceHandled(callbackRequestMock);
        verify(caseDataTransformerMock).transformIhtFormCaseDataByDeceasedDOD(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformCaseDataForEvidenceHandledCW() throws NotificationClientException {
        String paperFormValue = "Any";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
                .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER)))
                .thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
                bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(adColligendaBonaCaseTypeValidationRule, times(1)).validate(caseDetailsMock);
        verify(caseDataTransformerMock).transformCaseDataForEvidenceHandledForManualCreateByCW(callbackRequestMock);
    }

    @Test
    void shouldValidateFurtherEvidenceForApplication() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
                .thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =
                underTest.solsValidateAdmon(callbackRequestMock, bindingResultMock, httpServletRequest);
        verify(furtherEvidenceForApplicationValidationRule, times(1))
                .validate(caseDetailsMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSetupRegistrarsDecision() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.setupRegistrarsDecision(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(1))
                .transformCaseWithRegistrarDirection(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldInvokeRegistrarsDecision() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        ResponseEntity<CallbackResponse> response =
                underTest.registrarsDecision(callbackRequestMock);
        verify(registrarDirectionServiceMock, times(1)).addAndOrderDirectionsToGrant(caseDataMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldInvokeCaseWorkerEscalation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        ResponseEntity<CallbackResponse> response =
                underTest.caseworkerEscalated(callbackRequestMock, bindingResultMock, httpServletRequest);
        verify(caseEscalatedServiceMock, times(1)).setCaseWorkerEscalatedDate(caseDetailsMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldInvokeCaseWorkerResolveEscalation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        ResponseEntity<CallbackResponse> response =
                underTest.resolveCaseworkerEscalated(callbackRequestMock, bindingResultMock,
                        httpServletRequest);
        verify(caseEscalatedServiceMock, times(1)).setResolveCaseWorkerEscalatedDate(caseDetailsMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldChangeCaseState() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.changeCaseState(callbackRequestMock, httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .transferToState(callbackRequestMock, CASEWORKER_USERINFO);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    void shouldResolveCaveatState() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.resolveCaveatStopState(callbackRequestMock,httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .transferCaveatStopState(eq(callbackRequestMock), any());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformUniqueCode() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.validateUniqueProbateCode(callbackRequestMock, httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .transformUniqueProbateCode(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformValuesPage() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.validateValuesPage(callbackRequestMock,httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .transformValuesPage(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldRollback() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.rollbackDataMigration(callbackRequestMock, httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .rollback(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformDOB() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.changeDob(callbackRequestMock, httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .changeDob(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformChannelChoice() throws NotificationClientException {
        ResponseCaseData responseCaseData = ResponseCaseData.builder().channelChoice(PAPERFORM).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendAndCreateValidationRules))
                .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER)))
                .thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock, CASEWORKER_USERINFO))
                .thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response =
                underTest.paperFormCaseDetails(callbackRequestMock, bindingResultMock);

        verify(caseDataTransformerMock).transformCaseDataForPaperForm(callbackRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getChannelChoice(), is(PAPERFORM));
    }

    @Test
    void shouldTransformLastModifiedDateForDormant() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.setLastModifiedDateForDormant(callbackRequestMock);
        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformSuperUserMakeCaseForDormant() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        ResponseEntity<CallbackResponse> response =
                underTest.superUserMakeDormantCase(callbackRequestMock, httpServletRequest);
        verify(callbackResponseTransformerMock, times(1))
                .superUserMakeCaseDormant(callbackRequestMock, CASEWORKER_USERINFO);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendEmailAndAddDocumentForSolicitorOnUploadDocumentsEvent() throws NotificationClientException {
        when(callbackRequestMock.getEventId()).thenReturn(UPLOAD_DOCUMENT_EVENT.toUpperCase());
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(notificationService.sendStopResponseReceivedEmail(caseDetailsMock)).thenReturn(documentMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CallbackResponse> response = underTest.reactivateCase(callbackRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caseStoppedServiceMock).setEvidenceHandledNo(caseDetailsMock);
        verify(notificationService).sendStopResponseReceivedEmail(caseDetailsMock);
        verify(documentTransformerMock).addDocument(callbackRequestMock, documentMock, false);
        verify(callbackResponseTransformerMock).transformCase(callbackRequestMock, CASEWORKER_USERINFO);
    }

    @Test
    void shouldNotSendEmailForPAOnUploadDocumentsEvent() {
        when(callbackRequestMock.getEventId()).thenReturn(UPLOAD_DOCUMENT_EVENT.toUpperCase());
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CallbackResponse> response = underTest.reactivateCase(callbackRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caseStoppedServiceMock).setEvidenceHandledNo(caseDetailsMock);
        verify(callbackResponseTransformerMock).transformCase(callbackRequestMock, CASEWORKER_USERINFO);
    }

    @Test
    void shouldNotSendEmailForPAOnReactivateEvent() {
        when(callbackRequestMock.getEventId()).thenReturn("stopDormantCase");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CallbackResponse> response = underTest.reactivateCase(callbackRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caseStoppedServiceMock).setEvidenceHandledNo(caseDetailsMock);
        verify(callbackResponseTransformerMock).transformCase(callbackRequestMock, CASEWORKER_USERINFO);
    }

    @Test
    void shouldNotAddDocumentWhenNotificationClientExceptionThrown() throws NotificationClientException {
        when(callbackRequestMock.getEventId()).thenReturn(UPLOAD_DOCUMENT_EVENT.toUpperCase());
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(notificationService.sendStopResponseReceivedEmail(caseDetailsMock))
                .thenThrow(new NotificationClientException("Error sending email"));
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CallbackResponse> response = underTest.reactivateCase(callbackRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caseStoppedServiceMock).setEvidenceHandledNo(caseDetailsMock);
        verify(notificationService).sendStopResponseReceivedEmail(caseDetailsMock);
        verify(documentTransformerMock, times(0)).addDocument(callbackRequestMock, documentMock, false);
        verify(callbackResponseTransformerMock).transformCase(callbackRequestMock, CASEWORKER_USERINFO);
    }

    @Test
    void shouldAttemptToEmailWhenEnterPostGrantIssued() {

        final List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

        when(notificationService.sendPostGrantIssuedNotification(any()))
                .thenReturn(documentMock);

        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData())
                .thenReturn(caseDataMock);
        when(caseDataMock.getProbateNotificationsGenerated())
                .thenReturn(notificationsGenerated);

        ResponseEntity<CallbackResponse> response = underTest
                .moveToPostGrantIssue(callbackRequestMock, httpServletRequest);

        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);

        assertAll(
                () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(notificationsGenerated.size(), is(1)));
    }

    @Test
    void shouldAttemptToEmailWhenEscalateToRegistrar() throws RegistrarEscalationException {

        final List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

        when(notificationService.sendRegistrarEscalationNotification(any()))
                .thenReturn(documentMock);

        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData())
                .thenReturn(caseDataMock);
        when(caseDataMock.getProbateNotificationsGenerated())
                .thenReturn(notificationsGenerated);

        final ResponseEntity<CallbackResponse> response = underTest
                .caseEscalated(callbackRequestMock, bindingResultMock, httpServletRequest);

        verify(notificationService, times(1))
                .sendRegistrarEscalationNotification(any());
        verify(notificationService, never())
                .sendRegistrarEscalationNotificationFailed(any(), any());
        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);

        assertAll(
                () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(notificationsGenerated.size(), is(1)));
    }

    @Test
    void shouldSucceedWhenDocGenFailsEnterPostGrantIssued() {

        final List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

        when(notificationService.sendPostGrantIssuedNotification(any()))
                .thenReturn(null);

        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData())
                .thenReturn(caseDataMock);
        when(caseDataMock.getProbateNotificationsGenerated())
                .thenReturn(notificationsGenerated);

        ResponseEntity<CallbackResponse> response = underTest
                .moveToPostGrantIssue(callbackRequestMock, httpServletRequest);

        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);

        assertAll(
                () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(notificationsGenerated, empty()));
    }
  
    @Test
    void shouldAttemptToEmailCaseworkerWhenEscalateToRegistrarFails() throws RegistrarEscalationException {

        final List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

        final RegistrarEscalationException registrarEscalationExceptionMock = mock(RegistrarEscalationException.class);
        when(notificationService.sendRegistrarEscalationNotification(any()))
                .thenThrow(registrarEscalationExceptionMock);

        when(notificationService.sendRegistrarEscalationNotificationFailed(any(), any()))
                .thenReturn(documentMock);

        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData())
                .thenReturn(caseDataMock);
        when(caseDataMock.getProbateNotificationsGenerated())
                .thenReturn(notificationsGenerated);

        final ResponseEntity<CallbackResponse> response = underTest
                .caseEscalated(callbackRequestMock, bindingResultMock, httpServletRequest);

        verify(notificationService, times(1))
                .sendRegistrarEscalationNotification(any());
        verify(notificationService, times(1))
                .sendRegistrarEscalationNotificationFailed(any(), any());
        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);

        assertAll(
                () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(notificationsGenerated.size(), is(1)));
    }

    @Test
    void shouldNotAttemptToEmailCaseworkerWhenEscalateToRegistrarReturnsNoDocument()
            throws RegistrarEscalationException {

        final List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

        when(notificationService.sendRegistrarEscalationNotification(any()))
                .thenReturn(null);

        when(notificationService.sendRegistrarEscalationNotificationFailed(any(), any()))
                .thenReturn(documentMock);

        when(callbackRequestMock.getCaseDetails())
                .thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData())
                .thenReturn(caseDataMock);
        when(caseDataMock.getProbateNotificationsGenerated())
                .thenReturn(notificationsGenerated);

        final ResponseEntity<CallbackResponse> response = underTest
                .caseEscalated(callbackRequestMock, bindingResultMock, httpServletRequest);

        verify(notificationService, times(1))
                .sendRegistrarEscalationNotification(any());
        verify(notificationService, never())
                .sendRegistrarEscalationNotificationFailed(any(), any());
        verify(callbackResponseTransformerMock, times(1))
                .transformCase(callbackRequestMock, CASEWORKER_USERINFO);

        assertAll(
                () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(notificationsGenerated, empty()));
    }
}
