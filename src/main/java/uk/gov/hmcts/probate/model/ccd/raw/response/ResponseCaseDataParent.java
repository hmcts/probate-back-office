package uk.gov.hmcts.probate.model.ccd.raw.response;

import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

public class ResponseCaseDataParent {

    protected DynamicList reprintDocument;

    protected String reprintNumberOfCopies;

    protected DynamicList solsAmendLegalStatmentSelect;

    protected String declarationCheckbox;
    protected String ihtGrossValueField;
    protected String ihtNetValueField;
    protected Long numberOfExecutors;
    protected Long numberOfApplicants;
    protected String legalDeclarationJson;
    protected String checkAnswersSummaryJson;
    protected String registryAddress;
    protected String registryEmailAddress;
    protected String registrySequenceNumber;
    protected String qualifiedLegalProfessional;
    protected String SOTForenames;
    protected String SOTSurname;

    ResponseCaseDataParent() {
    }

    ResponseCaseDataParent(DynamicList reprintDocument, String reprintNumberOfCopies, DynamicList solsAmendLegalStatmentSelect, String declarationCheckbox, String ihtGrossValueField, String ihtNetValueField, Long numberOfExecutors, Long numberOfApplicants, String legalDeclarationJson,
                           String checkAnswersSummaryJson,
                           String registryAddress, String registryEmailAddress, String registrySequenceNumber, String qualifiedLegalProfessional, String SOTForenames, String SOTSurname) {
        this.reprintDocument = reprintDocument;
        this.reprintNumberOfCopies = reprintNumberOfCopies;
        this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
        this.declarationCheckbox = declarationCheckbox;
        this.ihtGrossValueField = ihtGrossValueField;
        this.ihtNetValueField = ihtNetValueField;
        this.numberOfExecutors = numberOfExecutors;
        this.numberOfApplicants = numberOfApplicants;
        this.legalDeclarationJson = legalDeclarationJson;
        this.checkAnswersSummaryJson = checkAnswersSummaryJson;
        this.registryAddress = registryAddress;
        this.registryEmailAddress = registryEmailAddress;
        this.registrySequenceNumber = registrySequenceNumber;
        this.qualifiedLegalProfessional = qualifiedLegalProfessional;
        this.SOTForenames = SOTForenames;
        this.SOTSurname = SOTSurname;
    }

    public DynamicList getReprintDocument() {
        return reprintDocument;
    }

    public String getReprintNumberOfCopies() {
        return reprintNumberOfCopies;
    }

    public DynamicList getSolsAmendLegalStatmentSelect() {
        return solsAmendLegalStatmentSelect;
    }

    public String getDeclarationCheckbox() {
        return declarationCheckbox;
    }
    
    public String getIhtNetValueField() { return ihtNetValueField; }

    public String getIhtGrossValueField() {
        return ihtGrossValueField;
    }

    public Long getNumberOfExecutors() {
        return numberOfExecutors;
    }

    public Long getNumberOfApplicants() {
        return numberOfApplicants;
    }

    public String getLegalDeclarationJson() {
        return legalDeclarationJson;
    }

    public String getCheckAnswersSummaryJson() {
        return checkAnswersSummaryJson;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public String getRegistryEmailAddress() {
        return registryEmailAddress;
    }

    public String getRegistrySequenceNumber() {
        return registrySequenceNumber;
    }

    public String getQualifiedLegalProfessional() {
        return qualifiedLegalProfessional;
    }

    public String getSOTForenames() {
        return SOTForenames;
    }

    public String getSOTSurname() {
        return SOTSurname;
    }

    public static ResponseCaseDataParentBuilder builder() {
        return new ResponseCaseDataParentBuilder();
    }

    public static class ResponseCaseDataParentBuilder {
        protected DynamicList reprintDocument;
        protected String reprintNumberOfCopies;
        protected DynamicList solsAmendLegalStatmentSelect;
        protected String declarationCheckbox;
        protected String ihtNetValueField;
        protected String ihtGrossValueField;
        protected Long numberOfExecutors;
        protected Long numberOfApplicants;
        protected String legalDeclarationJson;
        protected String checkAnswersSummaryJson;
        protected String registryAddress;
        protected String registryEmailAddress;
        protected String registrySequenceNumber;
        protected String qualifiedLegalProfessional;
        protected String SOTForenames;
        protected String SOTSurname;

        ResponseCaseDataParentBuilder() {
        }

        public ResponseCaseDataParentBuilder reprintDocument(DynamicList reprintDocument) {
            this.reprintDocument = reprintDocument;
            return this;
        }

        public ResponseCaseDataParentBuilder reprintNumberOfCopies(String reprintNumberOfCopies) {
            this.reprintNumberOfCopies = reprintNumberOfCopies;
            return this;
        }

        public ResponseCaseDataParentBuilder solsAmendLegalStatmentSelect(DynamicList solsAmendLegalStatmentSelect) {
            this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
            return this;
        }

        public ResponseCaseDataParentBuilder declarationCheckbox(String declarationCheckbox) {
            this.declarationCheckbox = declarationCheckbox;
            return this;
        }

        public ResponseCaseDataParentBuilder ihtNetValueField(String ihtNetValueField) {
            this.ihtNetValueField = ihtNetValueField;
            return this;
        }

        public ResponseCaseDataParentBuilder ihtGrossValueField(String ihtGrossValueField) {
            this.ihtGrossValueField = ihtGrossValueField;
            return this;
        }
        public ResponseCaseDataParentBuilder numberOfExecutors(Long numberOfExecutors) {
            this.numberOfExecutors = numberOfExecutors;
            return this;
        }
        public ResponseCaseDataParentBuilder numberOfApplicants(Long numberOfApplicants) {
            this.numberOfApplicants = numberOfApplicants;
            return this;
        }
        public ResponseCaseDataParentBuilder legalDeclarationJson(String legalDeclarationJson) {
            this.legalDeclarationJson = legalDeclarationJson;
            return this;
        }
        public ResponseCaseDataParentBuilder checkAnswersSummaryJson(String checkAnswersSummaryJson) {
            this.checkAnswersSummaryJson = checkAnswersSummaryJson;
            return this;
        }

        public ResponseCaseDataParentBuilder registryAddress(String registryAddress) {
            this.registryAddress = registryAddress;
            return this;
        }

        public ResponseCaseDataParentBuilder registryEmailAddress(String registryEmailAddress) {
            this.registryEmailAddress = registryEmailAddress;
            return this;
        }

        public ResponseCaseDataParentBuilder registrySequenceNumber(String registrySequenceNumber) {
            this.registrySequenceNumber = registrySequenceNumber;
            return this;
        }

        public ResponseCaseDataParentBuilder qualifiedLegalProfessional(String qualifiedLegalProfessional) {
            this.qualifiedLegalProfessional = qualifiedLegalProfessional;
            return this;
        }

        public ResponseCaseDataParentBuilder SOTForenames(String SOTForenames) {
            this.SOTForenames = SOTForenames;
            return this;
        }

        public ResponseCaseDataParentBuilder SOTSurname(String SOTSurname) {
            this.SOTSurname = SOTSurname;
            return this;
        }

        public ResponseCaseDataParent build() {
            return new ResponseCaseDataParent(reprintDocument, reprintNumberOfCopies, 
                solsAmendLegalStatmentSelect, declarationCheckbox, ihtGrossValueField, ihtNetValueField, 
                numberOfExecutors, numberOfApplicants, legalDeclarationJson, checkAnswersSummaryJson, 
                registryAddress, registryEmailAddress, registrySequenceNumber, qualifiedLegalProfessional, SOTForenames, SOTSurname);
        }
    }
}
