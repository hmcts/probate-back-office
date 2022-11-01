package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;
import uk.gov.hmcts.probate.service.payments.PaymentFeeBuilder;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class ServiceRequestTransformerTest {
    @InjectMocks
    private ServiceRequestTransformer serviceRequestTransformer;
    @Mock
    private PaymentFeeBuilder paymentFeeBuilder;
    @Mock
    private PaymentFee paymentFeeApplication;
    private CaseDetails caseDetails;
    private CaveatDetails caveatDetails;
    private FeesResponse feesResponse;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        feesResponse = FeesResponse.builder()
                .applicationFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(215)).code("appCode")
                        .description("appDesc").build())
                .ukCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(1)).code("ukCopiesCode")
                        .description("ukCopiesDesc").build())
                .overseasCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(2)).code("osCopiesCode")
                        .description("osCopiesDesc").build()).build();
    }

    private static Stream<Arguments> copies() {
        return Stream.of(arguments(null, null),
                arguments(1L, null),
                arguments(null, 1L)
                );
    }

    @ParameterizedTest
    @MethodSource("copies")
    void shouldBuildServiceRequestForGop(final Long extraCopies, final Long outsideUkCopies) {
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTForenames("Joe")
                .solsSOTSurname("Smith")
                .extraCopiesOfGrant(extraCopies)
                .outsideUKGrantCopies(outsideUkCopies)
                .build(),
                LAST_MODIFIED, CASE_ID);

        serviceRequestTransformer.buildServiceRequest(caseDetails, feesResponse);
    }

    @Test
    void shouldBuildServiceRequestWithApplicationFeeForCaveat() {
        caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorForenames("Joe")
                .caveatorSurname("Smith")
                .build(),
                LAST_MODIFIED, CASE_ID);
        feesResponse = FeesResponse.builder()
                .applicationFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(215)).code("appCode")
                        .description("appDesc").build()).build();

        serviceRequestTransformer.buildServiceRequest(caveatDetails, feesResponse.getApplicationFeeResponse());
    }
}
