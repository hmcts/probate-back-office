package uk.gov.hmcts.probate.model.ccd.raw.response;

import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

public class ResponseCaseDataParent {

    protected DynamicList reprintDocument;

    protected String reprintNumberOfCopies;

    protected DynamicList solsAmendLegalStatmentSelect;

    ResponseCaseDataParent() {
    }

    ResponseCaseDataParent(DynamicList reprintDocument, String reprintNumberOfCopies, DynamicList solsAmendLegalStatmentSelect) {
        this.reprintDocument = reprintDocument;
        this.reprintNumberOfCopies = reprintNumberOfCopies;
        this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
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

    public static ResponseCaseDataParentBuilder builder() {
        return new ResponseCaseDataParentBuilder();
    }

    public static class ResponseCaseDataParentBuilder {
        protected DynamicList reprintDocument;
        protected String reprintNumberOfCopies;
        protected DynamicList solsAmendLegalStatmentSelect;

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

        public ResponseCaseDataParent build() {
            return new ResponseCaseDataParent(reprintDocument, reprintNumberOfCopies, solsAmendLegalStatmentSelect);
        }

        public String toString() {
            return "ResponseCaseDataParent.ResponseCaseDataParentBuilder(reprintDocument=" + this.reprintDocument + ", reprintNumberOfCopies=" + this.reprintNumberOfCopies + ", solsAmendLegalStatmentSelect=" + solsAmendLegalStatmentSelect + ")";
        }
    }
}
