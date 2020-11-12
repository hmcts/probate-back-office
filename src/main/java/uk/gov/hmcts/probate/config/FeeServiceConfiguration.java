package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("fee.api")
public class FeeServiceConfiguration {

    private String url;
    private String api;
    private String service;
    private String jurisdiction1;
    private String jurisdiction2;
    private String channel;
    private String applicantType;
    private String keyword;
    private String newCopiesFeeKeyword;
    private String newIssuesFee5kKeyword;
    private String newIssuesFeeKeyword;
    private double ihtMinAmt;
}
