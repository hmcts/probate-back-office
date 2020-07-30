package uk.gov.hmcts.probate.model.ccd.raw.response;

import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

public class ResponseCaseDataParent {

    protected DynamicList reprintDocument;

    protected String reprintNumberOfCopies;

    protected DynamicList solsAmendLegalStatmentSelect;

    protected String ihtGrossValueField;
    protected String ihtNetValueField;
    protected Long numberOfExecutors;
    protected Long numberOfApplicants;
    protected String legalDeclarationJson;
    protected String checkAnswersSummaryJson;
    protected String registryAddress;
    protected String registryEmailAddress;
    protected String registrySequenceNumber;

    ResponseCaseDataParent() {
    }

    ResponseCaseDataParent(DynamicList reprintDocument, String reprintNumberOfCopies, DynamicList solsAmendLegalStatmentSelect, String ihtGrossValueField, String ihtNetValueField, Long numberOfExecutors, Long numberOfApplicants, String legalDeclarationJson, String checkAnswersSummaryJson, String registryAddress, String registryEmailAddress, String registrySequenceNumber) {
        this.reprintDocument = reprintDocument;
        this.reprintNumberOfCopies = reprintNumberOfCopies;
        this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
        this.ihtGrossValueField = ihtGrossValueField;
        this.ihtNetValueField = ihtNetValueField;
        this.numberOfExecutors = numberOfExecutors;
        this.numberOfApplicants = numberOfApplicants;
        this.legalDeclarationJson = legalDeclarationJson;
        this.checkAnswersSummaryJson = checkAnswersSummaryJson;
        this.registryAddress = registryAddress;
        this.registryEmailAddress = registryEmailAddress;
        this.registrySequenceNumber = registrySequenceNumber;
    }

    public DynamicList getReprintDocument() {
        return reprintDocument;
    }

    public void setReprintDocument(DynamicList reprintDocument) {
        this.reprintDocument = reprintDocument;
    }

    public String getReprintNumberOfCopies() {
        return reprintNumberOfCopies;
    }

    public void setReprintNumberOfCopies(String reprintNumberOfCopies) {
        this.reprintNumberOfCopies = reprintNumberOfCopies;
    }

    public DynamicList getSolsAmendLegalStatmentSelect() {
        return solsAmendLegalStatmentSelect;
    }

    public void setSolsAmendLegalStatmentSelect(DynamicList solsAmendLegalStatmentSelect) {
        this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
    }

    public String getIhtNetValueField() {
        return ihtNetValueField;
    }

    public void setIhtNetValueField(String ihtNetValueField) {
        this.ihtNetValueField = ihtNetValueField;
    }

    public String getIhtGrossValueField() {
        return ihtGrossValueField;
    }

    public void setIhtGrossValueField(String ihtGrossValueField) {
        this.ihtGrossValueField = ihtGrossValueField;
    }

    public Long getNumberOfExecutors() {
        return numberOfExecutors;
    }

    public void setNumberOfExecutors(Long numberOfExecutors) {
        this.numberOfExecutors = numberOfExecutors;
    }

    public Long getNumberOfApplicants() {
        return numberOfApplicants;
    }

    public void setNumberOfApplicants(Long numberOfApplicants) {
        this.numberOfApplicants = numberOfApplicants;
    }

    public String getLegalDeclarationJson() {
        return legalDeclarationJson;
    }

    public void setLegalDeclarationJson(String legalDeclarationJson) {
        this.legalDeclarationJson = legalDeclarationJson;
    }

    public String getCheckAnswersSummaryJson() {
        return checkAnswersSummaryJson;
    }

    public void setCheckAnswersSummaryJson(String checkAnswersSummaryJson) {
        this.checkAnswersSummaryJson = checkAnswersSummaryJson;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryEmailAddress() {
        return registryEmailAddress;
    }

    public void setRegistryEmailAddress(String registryEmailAddress) {
        this.registryEmailAddress = registryEmailAddress;
    }

    public String getRegistrySequenceNumber() {
        return registrySequenceNumber;
    }

    public void setRegistrySequenceNumber(String registrySequenceNumber) {
        this.registrySequenceNumber = registrySequenceNumber;
    }

    public static ResponseCaseDataParentBuilder builder() {
        return new ResponseCaseDataParentBuilder();
    }

    public static class ResponseCaseDataParentBuilder {
        protected DynamicList reprintDocument;
        protected String reprintNumberOfCopies;
        protected DynamicList solsAmendLegalStatmentSelect;
        protected String ihtNetValueField;
        protected String ihtGrossValueField;
        protected Long numberOfExecutors;
        protected Long numberOfApplicants;
        protected String legalDeclarationJson;
        protected String checkAnswersSummaryJson;
        protected String registryAddress;
        protected String registryEmailAddress;
        protected String registrySequenceNumber;

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

        public ResponseCaseDataParent build() {
            return new ResponseCaseDataParent(reprintDocument, reprintNumberOfCopies, 
                solsAmendLegalStatmentSelect, ihtNetValueField, ihtGrossValueField, 
                numberOfExecutors, numberOfApplicants, legalDeclarationJson, checkAnswersSummaryJson, 
                registryAddress, registryEmailAddress, registrySequenceNumber);
        }

        public String toString() {
            return "ResponseCaseDataParent.ResponseCaseDataParentBuilder(reprintDocument=" + this.reprintDocument + ", reprintNumberOfCopies=" + this.reprintNumberOfCopies + ", solsAmendLegalStatmentSelect=" + solsAmendLegalStatmentSelect + ")";
        }
    }
}
