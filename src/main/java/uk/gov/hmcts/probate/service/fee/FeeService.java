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
import uk.gov.hmcts.probate.exception.SocketException;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.FeatureToggleService;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class FeeService {

    private static final String FEE_API_EVENT_TYPE_ISSUE = "issue";
    private static final String FEE_API_EVENT_TYPE_COPIES = "copies";
    private static final String FEE_API_EVENT_TYPE_CAVEAT = "miscellaneous";
    private final FeeServiceConfiguration feeServiceConfiguration;
    private final RestTemplate restTemplate;
    private final FeatureToggleService featureToggleService;

    private static <T> T nonNull(@Nullable T result) {
        try {
            Assert.state(result != null, "Entity should be non null in FeeService");
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }
        return result;
    }

    public FeesResponse getAllFeesData(BigDecimal amountInPounds, Long ukCopies, Long nonUkCopies) {
        FeeResponse applicationFeeResponse;
        FeeResponse ukCopiesFeeResponse;
        FeeResponse nonUkCopiesFeeResponse;
        try {
            applicationFeeResponse = getApplicationFeeResponse(amountInPounds);
            ukCopiesFeeResponse = getCopiesFeeResponse(ukCopies);
            nonUkCopiesFeeResponse = getCopiesFeeResponse(nonUkCopies);
        } catch (SocketTimeoutException e) {
            throw new SocketException("Timeout occurred while calling Fee register service.Please try again later");
        }


        return FeesResponse.builder()
            .applicationFeeResponse(applicationFeeResponse)
            .ukCopiesFeeResponse(ukCopiesFeeResponse)
            .overseasCopiesFeeResponse(nonUkCopiesFeeResponse)
            .build();
    }

    public FeeResponse getApplicationFeeResponse(BigDecimal amountInPound) throws SocketTimeoutException {
        URI uri = buildUri(FEE_API_EVENT_TYPE_ISSUE, amountInPound.toString());
        ResponseEntity<FeeResponse> responseEntity = nonNull(restTemplate.getForEntity(uri, FeeResponse.class));
        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return buildZeroValueFee();
        }

        FeeResponse body = responseEntity.getBody();
        if (body == null) {
            throw new ClientDataException("No Body in FeeService: getApplicationFee");
        } else {
            return body;
        }
    }

    public FeeResponse getCopiesFeeResponse(Long copies) throws SocketTimeoutException {
        if (copies == null) {
            return buildZeroValueFee();
        }

        URI uri = buildUri(FEE_API_EVENT_TYPE_COPIES, copies.toString());

        ResponseEntity<FeeResponse> responseEntity = nonNull(restTemplate.getForEntity(uri, FeeResponse.class));

        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return buildZeroValueFee();
        }

        FeeResponse body = responseEntity.getBody();
        if (body == null) {
            throw new ClientDataException("No Body in FeeService: getCopiesFee");
        } else {
            return body;
        }
    }

    public FeeResponse getCaveatFeesData() {
        URI uri = buildUri(FEE_API_EVENT_TYPE_CAVEAT, "0");
        ResponseEntity<FeeResponse> responseEntity = nonNull(restTemplate.getForEntity(uri, FeeResponse.class));
        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            return buildZeroValueFee();
        }

        FeeResponse body = responseEntity.getBody();
        if (body == null) {
            throw new ClientDataException("No Body in FeeService: getApplicationFee");
        } else {
            return body;
        }
    }

    private URI buildUri(String event, String amount) {
        UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(feeServiceConfiguration.getUrl() + feeServiceConfiguration.getApi())
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

        if (FEE_API_EVENT_TYPE_CAVEAT.equals(event)) {
            builder.queryParam("keyword", feeServiceConfiguration.getNewCaveat());
        }

        return builder.build().encode().toUri();
    }

    private FeeResponse buildZeroValueFee() {
        return FeeResponse.builder().feeAmount(BigDecimal.ZERO).build();
    }

}
