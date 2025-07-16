package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.probate.model.ccd.ocr.GORSolicitorMandatoryFields;

public interface CCDMandatoryFieldKeys {

    String MANDATORY_FIELD_WARNING_STRING = "%s (%s) is mandatory.";
    String MANDATORY_FIELD_NOT_FOUND_LOG = "{} was not found in ocr fields";
    String DEPENDANT_KEY_PRIMARYAPPLICANTALIAS = "primaryApplicantAlias";
    String DEPENDANT_DESC_PRIMARYAPPLICANTALIAS = "Primary applicant alias";
    String DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE = "ihtFormCompletedOnline";
    String DEPENDANT_DESC_IHTFORMCOMPLETEDONLINE = "IHT form completed online";
    String DEPENDANT_KEY_IHTREFERENCENUMBER = "ihtReferenceNumber";
    String DEPENDANT_DESC_IHTREFERENCENUMBER = "IHT reference number";
    String DEPENDANT_KEY_IHTFORMID = "ihtFormId";
    String DEPENDANT_DESC_IHTFORMID = "IHT form id";
    String DEPENDANT_KEY_PAPERPAYMENTMETHOD = "paperPaymentMethod";
    String DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER = "solsFeeAccountNumber";
    String DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER = "Solicitors fee account number";
    String DEPENDANT_KEY_SOLSWILLTYPE = "solsWillType";
    String DEPENDANT_KEY_SOLSWILLTYPEREASON = "solsWillTypeReason";
    String MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME =
        "executorsNotApplying_%s_notApplyingExecutorName";
    String DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON =
        "executorsNotApplying_%s_notApplyingExecutorReason";
    String DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON =
        "Executor %s not applying reason";
    String MANDATORY_KEY_FORM_VERSION =
            GORSolicitorMandatoryFields.FORM_VERSION.getKey();
    String SOLICTOR_KEY_IS_APPLYING = "solsSolicitorIsApplying";
    String SOLICTOR_KEY_REPRESENTATIVE_NAME = "solsSolicitorRepresentativeName";
    String SOLICTOR_KEY_FIRM_NAME = "solsSolicitorFirmName";
    String LEGAL_REPRESENTATIVE = "legalRepresentative";
}
