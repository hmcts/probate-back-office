package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
public enum ApplicationState {
    APP_CREATED("PAAppCreated", "PA application created"),
    SOL_APP_CREATED_SOLICITOR_DTLS("SolAppCreatedSolicitorDtls","Application created (solicitor details)"),
    SOL_APP_CREATED_DECEASED_DTLS("SolAppCreatedDeceasedDtls","Application created (deceased details)"),
    SOL_APP_UPDATED("SolAppUpdated", "Application updated"),
    SOL_PROBATE_CREATED("SolProbateCreated", "Grant of probate created"),
    SOL_INTESTACY_CREATED("SolIntestacyCreated", "Intestacy grant created"),
    SOL_ADMON_CREATED("SolAdmonCreated", "Admon will grant created"),
    CASE_CREATED("CaseCreated", "Case created"),

    CASE_PAYMENT_FAILED("CasePaymentFailed","Case payment failed"),
    STOPPED("Stopped","Stopped"),
    CASE_PRINTED("CasePrinted", "Awaiting documentation"),

    READY_FOR_EXAMINATION("BOReadyForExamination","Ready for examination"),
    EXAMINING("BOExamining","Examining"),

    BO_CASE_STOPPED("BOCaseStopped","Case stopped"),
    CAVEAT_PERMENANT("BOCaveatPermenant", "Caveat permenant"),
    REGISTRAR_ESCALATION("BORegistrarEscalation", "Registrar escalation"),

    READY_TO_ISSUE("BOReadyToIssue", "Ready to issue"),
    CASE_QA("BOCaseQA", "Case selected for QA"),

    CASE_MATCHING_ISSUE_GRANT("BOCaseMatchingIssueGrant", "Case Matching (Issue grant)"),
    CASE_MATCHING_EXAMINING("BOCaseMatchingExamining","Case Matching (Examining)"),

    GRANT_ISSUED("BOGrantIssued","Grant issued"),
    CASE_CLOSED("BOCaseClosed","Case closed"),
    PAPER_GRANT("applyforGrantPaperApplication","PA1P/PA1A/Solicitors"),

    CASE_IMPORTED("BOCaseImported","Case imported"),
    EXAMINING_REISSUE("BOExaminingReissue","Examining (reissue)"),
    CASE_MATCHING_REISSUE("BOCaseMatchingReissue","Case Matching (Reissue grant)"),
    CASE_STOPPED_REISSUE("BOCaseStoppedReissue","Case stopped (reissue)"),
    CASE_STOPPED_AWAIT_REDEC("BOCaseStoppedAwaitRedec","Awaiting redeclaration");

    private String id;
    private String name;

    ApplicationState(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
