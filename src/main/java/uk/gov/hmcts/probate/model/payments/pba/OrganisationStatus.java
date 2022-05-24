package uk.gov.hmcts.probate.model.payments.pba;

public enum OrganisationStatus {
    PENDING,
    ACTIVE,
    BLOCKED,
    REVIEW,
    DELETED;

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isReview() {
        return this == REVIEW;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
