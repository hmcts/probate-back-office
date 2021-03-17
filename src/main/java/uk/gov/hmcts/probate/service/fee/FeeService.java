package uk.gov.hmcts.probate.service.fee;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.fee.Fee;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.FeatureToggleService;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.net.URI;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeServiceConfiguration feeServiceConfiguration;
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final FeatureToggleService featureToggleService;

    private static final String FEE_API_EVENT_TYPE_ISSUE = "issue";
    private static final String FEE_API_EVENT_TYPE_COPIES = "copies";

    public BigDecimal getApplicationFee(BigDecimal amountInPound) {
        URI uri = buildUri(FEE_API_EVENT_TYPE_ISSUE, amountInPound.toString());
        appInsights.trackEvent(REQUEST_SENT, uri.toString());
        ResponseEntity<Fee> responseEntity = nonNull(restTemplate.getForEntity(uri, Fee.class));

        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return BigDecimal.ZERO;
        }

        Fee body = responseEntity.getBody();
        if( body == null){
            throw new ClientDataException("No Body in FeeService: getApplicationFee");
        }
        else{
            return body.getFeeAmount();
        }

      }

    public BigDecimal getCopiesFee(Long copies) {
        if (copies == null) {
            return BigDecimal.ZERO;
        }

        URI uri = buildUri(FEE_API_EVENT_TYPE_COPIES, copies.toString());

        ResponseEntity<Fee> responseEntity = nonNull(restTemplate.getForEntity(uri, Fee.class));

        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return BigDecimal.ZERO;
        }

        Fee body = responseEntity.getBody();
        if(body == null){
            throw new ClientDataException("No Body in FeeService: getCopiesFee");
        }
        else{
            return body.getFeeAmount();
        }
    }

    public FeeServiceResponse getTotalFee(BigDecimal amountInPounds, Long ukCopies, Long nonUkCopies) {
        BigDecimal applicationFee = getApplicationFee(amountInPounds);
        BigDecimal ukCopiesFee = getCopiesFee(ukCopies);
        BigDecimal nonUkCopiesFee = getCopiesFee(nonUkCopies);
        return FeeServiceResponse.builder()
            .applicationFee(applicationFee)
            .feeForUkCopies(ukCopiesFee)
            .feeForNonUkCopies(nonUkCopiesFee)
            .total(applicationFee.add(ukCopiesFee).add(nonUkCopiesFee))
            .build();
    }

    private URI buildUri(String event, String amount) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(feeServiceConfiguration.getUrl() + feeServiceConfiguration.getApi())
            .queryParam("service", feeServiceConfiguration.getService())
            .queryParam("jurisdiction1", feeServiceConfiguration.getJurisdiction1())
            .queryParam("jurisdiction2", feeServiceConfiguration.getJurisdiction2())
            .queryParam("channel", feeServiceConfiguration.getChannel())
            .queryParam("applicant_type", feeServiceConfiguration.getApplicantType())
            .queryParam("event", event)
            .queryParam("amount_or_volume", amount);

        if (FEE_API_EVENT_TYPE_ISSUE.equals(event) && featureToggleService.isNewFeeRegisterCodeEnabled()) {
            double amountDouble = Double.valueOf(amount);
            if (amountDouble > feeServiceConfiguration.getIhtMinAmt()) {
                builder.queryParam("keyword", feeServiceConfiguration.getNewIssuesFeeKeyword());
            } else {
                builder.queryParam("keyword", feeServiceConfiguration.getNewIssuesFee5kKeyword());
            }
        }

        if (FEE_API_EVENT_TYPE_COPIES.equals(event)) {
            if (featureToggleService.isNewFeeRegisterCodeEnabled()) {
                builder.queryParam("keyword", feeServiceConfiguration.getNewCopiesFeeKeyword());
            } else {
                builder.queryParam("keyword", feeServiceConfiguration.getKeyword());
            }
        }

        return builder.build().encode().toUri();
    }

    private static <T> T nonNull(@Nullable T result) {
        try {
            Assert.state(result != null, "Entity should be non null in FeeService");
        }catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }
        return result;
    }
}
