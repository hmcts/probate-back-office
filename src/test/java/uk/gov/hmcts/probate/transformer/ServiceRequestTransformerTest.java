package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;
import uk.gov.hmcts.probate.service.payments.PaymentFeeBuilder;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

class ServiceRequestTransformerTest {
    @InjectMocks
    private ServiceRequestTransformer serviceRequestTransformer;
    @Mock
    private PaymentFeeBuilder paymentFeeBuilder;
    @Mock
    private PaymentFee paymentFeeApplication;
    private CaseDetails caseDetails;
    private FeesResponse feesResponse;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldBuildServiceRequestWithApplicationFee() {
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTForenames("Joe")
                .solsSOTSurname("Smith")
                .build(),
                LAST_MODIFIED, CASE_ID);
        feesResponse = FeesResponse.builder()
                .applicationFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(215)).code("appCode")
                        .description("appDesc").build()).build();

        when(paymentFeeBuilder.buildPaymentFee(feesResponse.getApplicationFeeResponse(),
                BigDecimal.valueOf(1L))).thenReturn(paymentFeeApplication);

        serviceRequestTransformer.buildServiceRequest(caseDetails, feesResponse);
    }

    @Test
    void shouldBuildServiceRequestWithUkCopiesFee() {
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTForenames("Joe")
                .solsSOTSurname("Smith")
                .extraCopiesOfGrant(1L)
                .build(),
                LAST_MODIFIED, CASE_ID);
        feesResponse = FeesResponse.builder()
                .ukCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(1)).code("ukCopiesCode")
                .description("ukCopiesDesc").build())
                .build();

        serviceRequestTransformer.buildServiceRequest(caseDetails, feesResponse);
    }

    @Test
    void shouldBuildServiceRequestWithOverseasCopiesFee() {
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTForenames("Joe")
                .solsSOTSurname("Smith")
                .outsideUKGrantCopies(1L)
                .build(),
                LAST_MODIFIED, CASE_ID);
        feesResponse = FeesResponse.builder()
                .overseasCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(2)).code("osCopiesCode")
                        .description("osCopiesDesc").build())
                .build();

        serviceRequestTransformer.buildServiceRequest(caseDetails, feesResponse);
    }
}
