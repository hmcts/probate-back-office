package uk.gov.hmcts.probate.service.fee;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.model.fee.Fee;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;

import java.math.BigDecimal;
import java.net.URI;

@Data
@Service
public class FeeService {

    private final FeeServiceConfiguration feeServiceConfiguration;
    private final RestTemplate restTemplate;

    private static final String FEE_API_EVENT_TYPE_ISSUE = "issue";
    private static final String FEE_API_EVENT_TYPE_COPIES = "copies";

    public BigDecimal getApplicationFee(BigDecimal amountInPound) {
        URI uri = buildUri(FEE_API_EVENT_TYPE_ISSUE, amountInPound.toString());

        ResponseEntity<Fee> responseEntity = restTemplate.getForEntity(uri, Fee.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return BigDecimal.ZERO;
        }

        return responseEntity.getBody().getFeeAmount();
    }

    public BigDecimal getCopiesFee(Long copies) {
        if (copies == null) {
            return BigDecimal.ZERO;
        }

        URI uri = buildUri(FEE_API_EVENT_TYPE_COPIES, copies.toString());

        ResponseEntity<Fee> responseEntity = restTemplate.getForEntity(uri, Fee.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return BigDecimal.ZERO;
        }

        return responseEntity.getBody().getFeeAmount();
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
        return UriComponentsBuilder.fromHttpUrl(feeServiceConfiguration.getUrl() + feeServiceConfiguration.getApi())
            .queryParam("service", feeServiceConfiguration.getService())
            .queryParam("jurisdiction1", feeServiceConfiguration.getJurisdiction1())
            .queryParam("jurisdiction2", feeServiceConfiguration.getJurisdiction2())
            .queryParam("channel", feeServiceConfiguration.getChannel())
            .queryParam("applicant_type", feeServiceConfiguration.getApplicantType())
            .queryParam("event", event)
            .queryParam("amount_or_volume", amount)
            .build().encode().toUri();
    }
}
