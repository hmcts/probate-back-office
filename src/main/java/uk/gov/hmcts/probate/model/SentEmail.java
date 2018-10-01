package uk.gov.hmcts.probate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SentEmail {
    private final String sentOn;
    private final String from;
    private final String to;
    private final String subject;
    private final String body;
}
