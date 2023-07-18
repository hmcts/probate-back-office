package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;
import uk.gov.hmcts.probate.model.payments.servicerequest.CasePaymentRequestDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.service.payments.PaymentFeeBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

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
    private FeeResponse feeResponse;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    @Value("${payment.serviceRequest.hmctsOrgId}")
    private String hmctsOrgId;
    @Value("${payment.serviceRequest.GrantOfRepresentationCallbackUrl}")
    private String grantOfRepresentationCallback;
    @Value("${payment.serviceRequest.CaveatCallbackUrl}")
    private String caveatCallback;

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
                arguments(null, 1L),
                arguments(1L, 1L)
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
                .solsPBAPaymentReference("SOL-PBA-12345")
                .build(),
                LAST_MODIFIED, CASE_ID);
        CasePaymentRequestDto casePayentRequestDto = CasePaymentRequestDto.builder()
                .responsibleParty("Joe Smith").action("payment attempt created").build();
        List<PaymentFee> paymentFees = serviceRequestTransformer.buildFees(caseDetails.getData(), feesResponse);
        ServiceRequestDto serviceRequestDto = serviceRequestTransformer.buildServiceRequest(caseDetails, feesResponse);

        assertEquals(casePayentRequestDto, serviceRequestDto.getCasePaymentRequest());
        assertEquals(paymentFees, serviceRequestDto.getFees());
        assertEquals(grantOfRepresentationCallback, serviceRequestDto.getCallbackUrl());
        assertEquals("SOL-PBA-12345", serviceRequestDto.getCaseReference());
        assertEquals(CASE_ID.toString(), serviceRequestDto.getCcdCaseNumber());
        assertEquals(hmctsOrgId, serviceRequestDto.getHmctsOrgId());
    }

    @Test
    void shouldBuildServiceRequestWithApplicationFeeForCaveat() {
        caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorForenames("Joe")
                .caveatorSurname("Smith")
                .solsPBAPaymentReference("SOL-PBA-12345")
                .build(),
                LAST_MODIFIED, CASE_ID);
        when(paymentFeeBuilder.buildPaymentFee(feeResponse, BigDecimal.ONE)).thenReturn(paymentFeeApplication);
        CasePaymentRequestDto casePaymentRequestDto = CasePaymentRequestDto.builder()
                .responsibleParty("Joe Smith").action("payment attempt created").build();
        List<PaymentFee> fees = new ArrayList<>();
        fees.add(paymentFeeApplication);
        ServiceRequestDto serviceRequestDto = serviceRequestTransformer.buildServiceRequest(caveatDetails, feeResponse);
        assertEquals(fees, serviceRequestDto.getFees());
        assertEquals(casePaymentRequestDto, serviceRequestDto.getCasePaymentRequest());
        assertEquals(caveatCallback, serviceRequestDto.getCallbackUrl());
        assertEquals("SOL-PBA-12345", serviceRequestDto.getCaseReference());
        assertEquals(CASE_ID.toString(), serviceRequestDto.getCcdCaseNumber());
        assertEquals(hmctsOrgId, serviceRequestDto.getHmctsOrgId());
    }
}
