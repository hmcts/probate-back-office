package uk.gov.hmcts.probate.service.taskList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;

import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.*;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.*;


public class TaskListUpdateServiceTest {
    private final TaskListUpdateService taskListSvc = new TaskListUpdateService();
    private CaseData.CaseDataBuilder caseDataBuilder;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_HAS_CODICILS = "Yes";
    private static final String NUMBER_OF_CODICILS = "1";
    private static final String SOLICITOR_FORENAMES = "Peter";
    private static final String SOLICITOR_SURNAME = "Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String SOLS_NOT_APPLYING_REASON = "Power reserved";
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = CTSC;

    public static final String ANSWER_YES = "Yes";
    public static final String ANSWER_NO = "No";
    public static final String QA_CASE_STATE = "BOCaseQA";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String OTHER = "other";
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private static final List<CollectionMember<EstateItem>> UK_ESTATE = Arrays.asList(
            new CollectionMember<>(null,
                    EstateItem.builder()
                            .item("Item")
                            .value("999.99")
                            .build()));

    private static final DocumentLink SCANNED_DOCUMENT_URL = DocumentLink.builder()
            .documentBinaryUrl("http://somedoc")
            .documentFilename("somedoc.pdf")
            .documentUrl("http://somedoc/location")
            .build();

    private static final LocalDateTime scannedDate = LocalDateTime.parse("2018-01-01T12:34:56.123");
    private static final List<CollectionMember<ScannedDocument>> SCANNED_DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    ScannedDocument.builder()
                            .fileName("scanneddocument.pdf")
                            .controlNumber("1234")
                            .scannedDate(scannedDate)
                            .type("other")
                            .subtype("will")
                            .url(SCANNED_DOCUMENT_URL)
                            .build()));

    private final String expectedDefaultHtml = ""; // TODO set this when TaskList (showing a list of tasks and their status for case progress) story coded

    private static final String expectedStoppedHtml = "<div class=\"width-50\">\n\n<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case stopped</div>\n" +
            "\n" +
            "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n" +
            "<p class=\"govuk-body-s\">The case was stopped on Unknown for one of two reasons:</p>\n" +
            "<ul class=\"govuk-list govuk-list--bullet\">\n" +
            "<li>an internal review is needed</li>\n" +
            "<li>further information from the applicant or solicitor is needed</li>\n" +
            "</ul>\n" +
            "\n" +
            "<p class=\"govuk-body-s\">You will be notified by email if we need any information from you to progress the case.</p>\n" +
            "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been stopped for 4 weeks or more and you have not received any communication since then.</p>\n\n" +
            "<h2 class=\"govuk-heading-l\">Get help with your application</h2>\n\n" +
            "<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n" +
            "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call.</p><br/><p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">Monday to Thursday, 8:00am to 5pm</p><p class=\"govuk-body-s\">Friday, 8am to 4:30pm</p><br/><p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">Monday to Friday, 8:00am to 5pm</p><br/>\n\n" +
            "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Find out about call charges</a>\n\n" +
            "<h3 class=\"govuk-heading-m\">Email</h3>\n\n" +
            "<a href=\"mailto:contactprobate@justice.gov.uk\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">contactprobate@justice.gov.uk</a><p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n" +
            "</div>";

    private final String expectedEscalatedHtml = "<div class=\"width-50\">\n\n<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case escalated to the Registrar</div>\n" +
            "\n" +
            "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n" +
            "<p class=\"govuk-body-s\">The case was escalated on Unknown.</p>\n" +
            "<p class=\"govuk-body-s\">The case will be reviewed by the Registrar and you will be notified by email if we need any information from you to progress the case.</p>\n" +
            "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been escalated for 6 weeks or more and you have not received any communication since then.</p>\n\n\n" +
            "<h2 class=\"govuk-heading-l\">Get help with your application</h2>\n\n" +
            "<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n" +
            "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call.</p><br/><p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">Monday to Thursday, 8:00am to 5pm</p><p class=\"govuk-body-s\">Friday, 8am to 4:30pm</p><br/><p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">Monday to Friday, 8:00am to 5pm</p><br/>\n\n" +
            "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Find out about call charges</a>\n\n" +
            "<h3 class=\"govuk-heading-m\">Email</h3>\n\n" +
            "<a href=\"mailto:contactprobate@justice.gov.uk\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">contactprobate@justice.gov.uk</a><p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n" +
            "</div>";

    private String getPrimaryApplicantHasAlias(CaseData caseData) {
        if (PERSONAL.equals(caseData.getApplicationType())) {
            return ANSWER_NO;
        } else {
            return caseData.getPrimaryApplicantHasAlias();
        }
    }

    private String getOtherExecutorExists(CaseData caseData) {
        if (PERSONAL.equals(caseData.getApplicationType())) {
            return caseData.getAdditionalExecutorsApplying() == null || caseData.getAdditionalExecutorsApplying().isEmpty()
                    ? ANSWER_NO : ANSWER_YES;
        } else {
            return caseData.getOtherExecutorExists();
        }
    }

    private String transformToString(BigDecimal bdValue) {
        return ofNullable(bdValue)
                .map(BigDecimal::intValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private String transformToString(Long longValue) {
        return ofNullable(longValue)
                .map(String::valueOf)
                .orElse(null);
    }


    private ResponseCaseData.ResponseCaseDataBuilder getResponseCaseData(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();

        ResponseCaseData.ResponseCaseDataBuilder builder = ResponseCaseData.builder()
                .applicationType(ofNullable(caseData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(ofNullable(caseData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
                .deceasedForenames(caseData.getDeceasedForenames())
                .deceasedSurname(caseData.getDeceasedSurname())
                .deceasedDateOfBirth(ofNullable(caseData.getDeceasedDateOfBirth())
                        .map(dateTimeFormatter::format).orElse(null))
                .deceasedDateOfDeath(ofNullable(caseData.getDeceasedDateOfDeath())
                        .map(dateTimeFormatter::format).orElse(null))
                .willExists(caseData.getWillExists())
                .willAccessOriginal((caseData.getWillAccessOriginal()))
                .willHasCodicils(caseData.getWillHasCodicils())
                .willNumberOfCodicils(caseData.getWillNumberOfCodicils())
                .ihtFormId(caseData.getIhtFormId())
                .primaryApplicantForenames(caseData.getPrimaryApplicantForenames())
                .primaryApplicantSurname(caseData.getPrimaryApplicantSurname())
                .primaryApplicantEmailAddress(caseData.getPrimaryApplicantEmailAddress())
                .primaryApplicantIsApplying(caseData.getPrimaryApplicantIsApplying())
                .solsPrimaryExecutorNotApplyingReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
                .primaryApplicantHasAlias(getPrimaryApplicantHasAlias(caseData))
                .otherExecutorExists(getOtherExecutorExists(caseData))
                .primaryApplicantSameWillName(caseData.getPrimaryApplicantSameWillName())
                .primaryApplicantAliasReason(caseData.getPrimaryApplicantAliasReason())
                .primaryApplicantOtherReason(caseData.getPrimaryApplicantOtherReason())
                .deceasedAddress(caseData.getDeceasedAddress())
                .deceasedAnyOtherNames(caseData.getDeceasedAnyOtherNames())
                .primaryApplicantAddress(caseData.getPrimaryApplicantAddress())
                .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
                .caseMatches(caseData.getCaseMatches())

                .solsSOTNeedToUpdate(caseData.getSolsSOTNeedToUpdate())

                .ihtGrossValue(caseData.getIhtGrossValue())
                .ihtNetValue(caseData.getIhtNetValue())
                .deceasedDomicileInEngWales(caseData.getDeceasedDomicileInEngWales())

                .solsPaymentMethods(caseData.getSolsPaymentMethods())
                .solsFeeAccountNumber(caseData.getSolsFeeAccountNumber())

                .extraCopiesOfGrant(transformToString(caseData.getExtraCopiesOfGrant()))
                .outsideUKGrantCopies(transformToString(caseData.getOutsideUKGrantCopies()))
                .feeForNonUkCopies(transformToString(caseData.getFeeForNonUkCopies()))
                .feeForUkCopies(transformToString(caseData.getFeeForUkCopies()))
                .applicationFee(transformToString(caseData.getApplicationFee()))
                .totalFee(transformToString(caseData.getTotalFee()))

                .solsLegalStatementDocument(caseData.getSolsLegalStatementDocument())
                .casePrinted(caseData.getCasePrinted())
                .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
                .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotificationRequested())
                .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotification())
                .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotification())

                .boCaseStopReasonList(caseData.getBoCaseStopReasonList())
                .boStopDetails(caseData.getBoStopDetails())

                .boDeceasedTitle(caseData.getBoDeceasedTitle())
                .boDeceasedHonours(caseData.getBoDeceasedHonours())

                .ihtFormCompletedOnline(caseData.getIhtFormCompletedOnline())

                .boWillMessage(caseData.getBoWillMessage())
                .boExecutorLimitation(caseData.getBoExecutorLimitation())
                .boAdminClauseLimitation(caseData.getBoAdminClauseLimitation())
                .boLimitationText(caseData.getBoLimitationText())
                .probateDocumentsGenerated(caseData.getProbateDocumentsGenerated())
                .probateNotificationsGenerated(caseData.getProbateNotificationsGenerated())
                .boDocumentsUploaded(caseData.getBoDocumentsUploaded())

                .primaryApplicantPhoneNumber(caseData.getPrimaryApplicantPhoneNumber())
                .declaration(caseData.getDeclaration())
                .legalStatement(caseData.getLegalStatement())
                .deceasedMarriedAfterWillOrCodicilDate(caseData.getDeceasedMarriedAfterWillOrCodicilDate())

                .boExaminationChecklistQ1(caseData.getBoExaminationChecklistQ1())
                .boExaminationChecklistQ2(caseData.getBoExaminationChecklistQ2())
                .boExaminationChecklistRequestQA(caseData.getBoExaminationChecklistRequestQA())

                .payments(caseData.getPayments())
                .deceasedMarriedAfterWillOrCodicilDate(caseData.getDeceasedMarriedAfterWillOrCodicilDate())
                .applicationSubmittedDate(caseData.getApplicationSubmittedDate())

                .scannedDocuments(caseData.getScannedDocuments())
                .evidenceHandled(caseData.getEvidenceHandled())

                .paperForm(caseData.getPaperForm())
                .languagePreferenceWelsh(caseData.getLanguagePreferenceWelsh())
                .caseType(caseData.getCaseType())
                .solsSolicitorIsExec(caseData.getSolsSolicitorIsExec())
                .solsSolicitorIsMainApplicant(caseData.getSolsSolicitorIsMainApplicant())
                .solsSolicitorIsApplying(caseData.getSolsSolicitorIsApplying())
                .solsSolicitorNotApplyingReason(caseData.getSolsSolicitorNotApplyingReason())
                .solsWillType(caseData.getSolsWillType())
                .solsApplicantRelationshipToDeceased(caseData.getSolsApplicantRelationshipToDeceased())
                .solsSpouseOrCivilRenouncing(caseData.getSolsSpouseOrCivilRenouncing())
                .solsAdoptedEnglandOrWales(caseData.getSolsAdoptedEnglandOrWales())
                .solsMinorityInterest(caseData.getSolsMinorityInterest())
                .solsApplicantSiblings(caseData.getSolsApplicantSiblings())
                .solsDiedOrNotApplying(caseData.getSolsDiedOrNotApplying())
                .solsEntitledMinority(caseData.getSolsEntitledMinority())
                .solsLifeInterest(caseData.getSolsLifeInterest())
                .solsResiduary(caseData.getSolsResiduary())
                .solsResiduaryType(caseData.getSolsResiduaryType())

                .boCaveatStopNotificationRequested(caseData.getBoCaveatStopNotificationRequested())
                .boCaveatStopNotification(caseData.getBoCaveatStopNotification())

                .boCaseStopCaveatId(caseData.getBoCaseStopCaveatId())

                .boCaveatStopEmailNotificationRequested(caseData.getBoCaveatStopEmailNotificationRequested())
                .boCaveatStopEmailNotification(caseData.getBoCaveatStopEmailNotification())
                .boCaveatStopSendToBulkPrintRequested(caseData.getBoCaveatStopSendToBulkPrintRequested())
                .boCaveatStopSendToBulkPrint(caseData.getBoCaveatStopSendToBulkPrint())
                .boEmailGrantReissuedNotification(caseData.getBoEmailGrantReissuedNotification())
                .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
                .boGrantReissueSendToBulkPrint(caseData.getBoGrantReissueSendToBulkPrint())
                .boGrantReissueSendToBulkPrintRequested(caseData.getBoGrantReissueSendToBulkPrintRequested())
                .boAssembleLetterSendToBulkPrint(caseData.getBoAssembleLetterSendToBulkPrint())
                .boAssembleLetterSendToBulkPrintRequested(caseData.getBoAssembleLetterSendToBulkPrintRequested())

                .recordId(caseData.getRecordId())
                .legacyType(caseData.getLegacyType())
                .legacyCaseViewUrl(caseData.getLegacyCaseViewUrl())
                .grantIssuedDate(caseData.getGrantIssuedDate())
                .dateOfDeathType(caseData.getDateOfDeathType())
                .orderNeeded(caseData.getOrderNeeded())
                .reissueReason(caseData.getReissueReason())
                .reissueDate(caseData.getReissueDate())
                .reissueReasonNotation(caseData.getReissueReasonNotation())
                .latestGrantReissueDate(caseData.getLatestGrantReissueDate())
                .bulkPrintId(caseData.getBulkPrintId())

                .deceasedDivorcedInEnglandOrWales(caseData.getDeceasedDivorcedInEnglandOrWales())
                .primaryApplicantAdoptionInEnglandOrWales(caseData.getPrimaryApplicantAdoptionInEnglandOrWales())
                .deceasedSpouseNotApplyingReason(caseData.getDeceasedSpouseNotApplyingReason())
                .deceasedOtherChildren(caseData.getDeceasedOtherChildren())
                .allDeceasedChildrenOverEighteen(caseData.getAllDeceasedChildrenOverEighteen())
                .anyDeceasedChildrenDieBeforeDeceased(caseData.getAnyDeceasedChildrenDieBeforeDeceased())
                .anyDeceasedGrandChildrenUnderEighteen(caseData.getAnyDeceasedGrandChildrenUnderEighteen())
                .deceasedAnyChildren(caseData.getDeceasedAnyChildren())
                .deceasedHasAssetsOutsideUK(caseData.getDeceasedHasAssetsOutsideUK())
                .statementOfTruthDocument(caseData.getStatementOfTruthDocument())
                .boStopDetailsDeclarationParagraph(caseData.getBoStopDetailsDeclarationParagraph())
                .executorsApplyingNotifications(caseData.getExecutorsApplyingNotifications())
                .boEmailRequestInfoNotification(caseData.getBoEmailRequestInfoNotification())
                .boEmailRequestInfoNotificationRequested(caseData.getBoEmailRequestInfoNotificationRequested())
                .boRequestInfoSendToBulkPrint(caseData.getBoRequestInfoSendToBulkPrint())
                .boRequestInfoSendToBulkPrintRequested(caseData.getBoRequestInfoSendToBulkPrintRequested())
                .probateSotDocumentsGenerated(caseData.getProbateSotDocumentsGenerated())
                .bulkScanCaseReference(caseData.getBulkScanCaseReference())
                .grantDelayedNotificationIdentified(caseData.getGrantDelayedNotificationIdentified())
                .grantDelayedNotificationDate(ofNullable(caseData.getGrantDelayedNotificationDate())
                        .map(dateTimeFormatter::format).orElse(null))
                .grantStoppedDate(ofNullable(caseData.getGrantStoppedDate())
                        .map(dateTimeFormatter::format).orElse(null))
                .grantDelayedNotificationSent(caseData.getGrantDelayedNotificationSent())
                .grantAwaitingDocumentationNotificationDate(ofNullable(caseData.getGrantAwaitingDocumentationNotificationDate())
                        .map(dateTimeFormatter::format).orElse(null))
                .grantAwaitingDocumentatioNotificationSent(caseData.getGrantAwaitingDocumentatioNotificationSent())
                .pcqId(caseData.getPcqId())

                .reprintDocument(caseData.getReprintDocument())
                .reprintNumberOfCopies(caseData.getReprintNumberOfCopies())
                .solsAmendLegalStatmentSelect(caseData.getSolsAmendLegalStatmentSelect())
                .declarationCheckbox(caseData.getDeclarationCheckbox())
                .ihtGrossValueField(caseData.getIhtGrossValueField())
                .ihtNetValueField(caseData.getIhtNetValueField())
                .numberOfExecutors(caseData.getNumberOfExecutors())
                .numberOfApplicants(caseData.getNumberOfApplicants())
                .legalDeclarationJson(caseData.getLegalDeclarationJson())
                .checkAnswersSummaryJson(caseData.getCheckAnswersSummaryJson())
                .registryAddress(caseData.getRegistryAddress())
                .registryEmailAddress(caseData.getRegistryEmailAddress())
                .registrySequenceNumber(caseData.getRegistrySequenceNumber())
                .taskList(caseData.getTaskList())
                .escalatedDate(ofNullable(caseData.getEscalatedDate())
                        .map(dateTimeFormatter::format).orElse(null));

        return builder;
    }


    @Before
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        SolsAddress solsAddress = SolsAddress.builder()
                .addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE)
                .build();

        caseDataBuilder = CaseData.builder()
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .deceasedForenames(FORENAME)
                .deceasedSurname(SURNAME)
                .deceasedAddress(DECEASED_ADDRESS)
                .deceasedAnyOtherNames(DECEASED_OTHER_NAMES)
                .deceasedDomicileInEngWales(DECEASED_DOM_UK)
                .primaryApplicantForenames(PRIMARY_FORENAMES)
                .primaryApplicantSurname(PRIMARY_SURNAME)
                .primaryApplicantAddress(PRIMARY_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_APPLICANT_APPLYING)
                .primaryApplicantHasAlias(PRIMARY_APPLICANT_HAS_ALIAS)
                .otherExecutorExists(OTHER_EXEC_EXISTS)
                .solsWillType(WILL_TYPE_PROBATE)
                .willExists(WILL_EXISTS)
                .willAccessOriginal(WILL_ACCESS_ORIGINAL)
                .ihtNetValue(NET)
                .ihtGrossValue(GROSS)
                .solsSolicitorAppReference(SOLICITOR_APP_REFERENCE)
                .willHasCodicils(WILL_HAS_CODICILS)
                .willNumberOfCodicils(NUMBER_OF_CODICILS)
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorAddress(solsAddress)
                .ukEstate(UK_ESTATE)
                .applicationGrounds(APPLICATION_GROUNDS)
                .willDispose(YES)
                .englishWill(NO)
                .appointExec(YES)
                .ihtFormId(IHT_FORM)
                .solsSOTForenames(SOLICITOR_FORENAMES)
                .solsSOTSurname(SOLICITOR_SURNAME)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLS_NOT_APPLYING_REASON)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .solsPaymentMethods(PAYMENT_METHOD)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE)
                .scannedDocuments(SCANNED_DOCUMENTS_LIST);
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_CaseCreated() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("CaseCreated");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        // TODO!
        // assertTrue(result.build().getTaskList().equals(expectedDefaultHtml));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOExamining() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOExamining");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        // TODO!
        // assertTrue(result.build().getTaskList().equals(expectedDefaultHtml));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStopped() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOCaseStopped");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        assertTrue(result.build().getTaskList().equals(expectedStoppedHtml));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStoppedReissue() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOCaseStoppedReissue");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        assertTrue(result.build().getTaskList().equals(expectedStoppedHtml));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStoppedAwaitRedec() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOCaseStoppedAwaitRedec");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        assertTrue(result.build().getTaskList().equals(expectedStoppedHtml));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BORegistrarEscalation() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BORegistrarEscalation");
        ResponseCaseData.ResponseCaseDataBuilder bldr = getResponseCaseData(caseDetails);

        ResponseCaseData.ResponseCaseDataBuilder result = taskListSvc.generateTaskList(caseDetails, bldr);
        assertTrue(result.build().getTaskList().equals(expectedEscalatedHtml));
    }
}
