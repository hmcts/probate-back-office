package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.probate.model.ccd.ocr.GORSolicitorMandatoryFields;

public interface CCDMandatoryFieldKeys {

    static final String MANDATORY_FIELD_WARNING_STIRNG = "%s (%s) is mandatory.";
    static final String DEPENDANT_KEY_PRIMARYAPPLICANTALIAS = "primaryApplicantAlias";
    static final String DEPENDANT_DESC_PRIMARYAPPLICANTALIAS = "Primary applicant alias";
    static final String DEPENDANT_KEY_IHTREFERENCENUMBER = "ihtReferenceNumber";
    static final String DEPENDANT_DESC_IHTREFERENCENUMBER = "IHT reference number";
    static final String DEPENDANT_KEY_IHTFORMID = "ihtFormId";
    static final String DEPENDANT_DESC_IHTFORMID = "IHT form id";
    static final String DEPENDANT_KEY_PAPERPAYMENTMETHOD = "paperPaymentMethod";
    static final String DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER = "solsFeeAccountNumber";
    static final String DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER = "Solicitors fee account number";
    static final String DEPENDANT_KEY_SOLSWILLTYPE = "solsWillType";
    static final String DEPENDANT_KEY_SOLSWILLTYPEREASON = "solsWillTypeReason";
    static final String MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME =
        "executorsNotApplying_%s_notApplyingExecutorName";
    static final String DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON =
        "executorsNotApplying_%s_notApplyingExecutorReason";
    static final String DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON =
        "Executor %s not applying reason";
    static final String MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS =
        GORSolicitorMandatoryFields.PRIMARY_APPLICANT_HAS_ALIAS.getKey();
    static final String MANDATORY_KEY_IHTFORMCOMPLETEDONLINE =
        GORSolicitorMandatoryFields.IHT_FORM_COMPLETED_ONLINE.getKey();
    static final String SOLICTOR_KEY_IS_APPLYING = "solsSolicitorIsApplying";
    static final String SOLICTOR_KEY_REPRESENTATIVE_NAME = "solsSolicitorRepresentativeName";
    static final String SOLICTOR_KEY_FIRM_NAME = "solsSolicitorFirmName";
}
