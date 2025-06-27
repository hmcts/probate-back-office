package uk.gov.hmcts.probate.service.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentServiceResponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.DemoInstanceToggleService;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED_NO_DOCS;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsService {

    private static final String SERVICE_REQUEST_REFERENCE_KEY = "service_request_reference";
    private static final String PAYMENT_SUMMARY = "Service request payment details updated on case";
    private static final String PAYMENT_COMMENT = "Service request payment status ";
    private static final String SRP_STATUS_PAID = "Paid";
    private final ServiceRequestClient serviceRequestClient;
    private final SecurityUtils securityUtils;
    private final CcdClientApi ccdClientApi;
    private final IdamApi idamApi;
    private final CasePaymentBuilder casePaymentBuilder;
    private final NotificationService notificationService;
    private final PDFManagementService pdfManagementService;
    private final DocumentTransformer documentTransformer;
    private final CaveatNotificationService caveatNotificationService;
    private final DemoInstanceToggleService demoInstanceToggleService;

    public String createServiceRequest(ServiceRequestDto serviceRequestDto) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        PaymentServiceResponse paymentServiceResponse = serviceRequestClient
                .createServiceRequest(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(), serviceRequestDto);
        return paymentServiceResponse.getServiceRequestReference();
    }

    public void updateCaseFromServiceRequest(ServiceRequestUpdateResponseDto response, CcdCaseType ccdCaseType) {
        String caseId = response.getCcdCaseNumber();
        log.info("Updating case for Service Request, caseId:{}", caseId);
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails retrievedCaseDetails =
                retrieveCaseDetailsAsCaseworker(caseId, ccdCaseType);

        SecurityDTO securityDTO = getCaseworkerSecurityDTO();
        if (demoInstanceToggleService.getCcdCaseType() == ccdCaseType) {
            GrantOfRepresentationData caseData = buildGrantData(retrievedCaseDetails, response);
            String paymentStatus = caseData.getPayments().get(caseData.getPayments().size() - 1)
                    .getValue().getStatus().getName();
            ccdClientApi.updateCaseAsCaseworker(ccdCaseType, caseId, retrievedCaseDetails.getLastModified(),
                    caseData, getEventIdByServiceRequestStatus(response.getServiceRequestStatus()),
                    securityDTO, PAYMENT_COMMENT + paymentStatus, PAYMENT_SUMMARY);
        } else if (CAVEAT == ccdCaseType) {
            CaveatData caveatData = buildCaveatData(retrievedCaseDetails, response);
            String paymentStatus = caveatData.getPayments().get(caveatData.getPayments().size() - 1)
                    .getValue().getStatus().getName();
            ccdClientApi.updateCaseAsCaseworker(ccdCaseType, caseId, retrievedCaseDetails.getLastModified(),
                    caveatData, getEventIdByServiceRequestStatus(response.getServiceRequestStatus()),
                    securityDTO, PAYMENT_COMMENT + paymentStatus, PAYMENT_SUMMARY);
        } else {
            throw new IllegalArgumentException("Service request payment for Case:" + caseId + " not valid CaseType:"
                    + ccdCaseType);
        }

        log.info("Updated Service Request on caseId:{}", caseId);

    }

    private uk.gov.hmcts.reform.ccd.client.model.CaseDetails retrieveCaseDetailsAsCaseworker(String caseId,
                                                                                             CcdCaseType ccdCaseType) {
        SecurityDTO securityDTO = getCaseworkerSecurityDTO();
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails retrievedCaseDetails = ccdClientApi
                .readForCaseWorker(ccdCaseType, caseId, securityDTO);
        log.info("Retrieved case for Service Request, caseId:{}", caseId);

        return retrievedCaseDetails;
    }

    private SecurityDTO getCaseworkerSecurityDTO() {
        securityUtils.setSecurityContextUserAsCaseworker();
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(securityUtils.getAuthorisation());
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        return SecurityDTO.builder().authorisation(securityUtils.getAuthorisation())
                .serviceAuthorisation(securityUtils.generateServiceToken())
                .userId(userId)
                .build();
    }

    private GrantOfRepresentationData buildGrantData(
            uk.gov.hmcts.reform.ccd.client.model.CaseDetails retrievedCaseDetails,
            ServiceRequestUpdateResponseDto response) {

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Map<String, Object> placeholders = mapper.convertValue(retrievedCaseDetails.getData(), Map.class);
        CaseData caseData = mapper.convertValue(placeholders, CaseData.class);

        CaseDetails caseDetails = new CaseDetails(caseData, null, retrievedCaseDetails.getId());
        caseDetails.setState(retrievedCaseDetails.getState());

        return isSuccessfulPayment(response.getServiceRequestStatus())
                ? buildGrantPaid(caseDetails, response) : buildGrantNotPaid(caseDetails, response);
    }

    private GrantOfRepresentationData buildGrantPaid(CaseDetails caseDetails,
                                                                 ServiceRequestUpdateResponseDto response) {
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        Document sentEmail = null;
        try {
            securityUtils.setSecurityContextUserAsCaseworker();
            if (!NO.equals(caseDetails.getData().getEvidenceHandled())) {
                notificationService.startAwaitingDocumentationNotificationPeriod(caseDetails);
                sentEmail = notificationService.sendEmail(APPLICATION_RECEIVED, caseDetails);
            } else {
                sentEmail = notificationService.sendEmail(APPLICATION_RECEIVED_NO_DOCS, caseDetails);
            }
        } catch (NotificationClientException e) {
            log.info("Case:" + caseDetails.getId() + "Payment service NotificationClientException: " + e.getMessage());
        }
        if (sentEmail != null) {
            if (null == sentEmail.getDocumentGeneratedBy()
                    && null != caseDetails.getData().getApplicationSubmittedBy()) {
                sentEmail.setDocumentGeneratedBy(caseDetails.getData().getApplicationSubmittedBy());
            }
            documentTransformer.addDocument(callbackRequest, sentEmail, false);
        }

        List<CollectionMember<CasePayment>> allPayments = casePaymentBuilder.addPaymentFromServiceRequestResponse(
                caseDetails.getData().getPayments(), response);

        Document coversheet = pdfManagementService
                .generateAndUpload(callbackRequest, DocumentType.SOLICITOR_COVERSHEET);

        return GrantOfRepresentationData.builder()
                .grantAwaitingDocumentationNotificationDate(caseDetails.getData()
                        .getGrantAwaitingDocumentationNotificationDate())
                .solsCoversheetDocument(getCoversheet(coversheet))
                .probateNotificationsGenerated(asNotificationsGenerated(callbackRequest.getCaseDetails().getData()
                        .getProbateNotificationsGenerated()))
                .payments(allPayments)
                .paymentTaken(getPaymentTakenStatus(caseDetails.getData().getPaymentTaken(),
                        response.getServiceRequestStatus()))
                .build();
    }

    private GrantOfRepresentationData buildGrantNotPaid(CaseDetails caseDetails,
                                                                 ServiceRequestUpdateResponseDto response) {
        List<CollectionMember<CasePayment>> allPayments = casePaymentBuilder.addPaymentFromServiceRequestResponse(
                caseDetails.getData().getPayments(), response);
        return GrantOfRepresentationData.builder()
                .payments(allPayments)
                .paymentTaken(getPaymentTakenStatus(caseDetails.getData().getPaymentTaken(),
                        response.getServiceRequestStatus()))
                .build();
    }

    private uk.gov.hmcts.reform.probate.model.cases.DocumentLink getCoversheet(Document coversheet) {
        if (coversheet == null) {
            return null;
        } else {
            return uk.gov.hmcts.reform.probate.model.cases.DocumentLink.builder()
                            .documentUrl(coversheet.getDocumentLink().getDocumentUrl())
                            .documentBinaryUrl(coversheet.getDocumentLink().getDocumentBinaryUrl())
                            .documentFilename(coversheet.getDocumentLink().getDocumentFilename())
                            .build();
        }
    }

    private CaveatData buildCaveatData(
            uk.gov.hmcts.reform.ccd.client.model.CaseDetails retrievedCaseDetails,
            ServiceRequestUpdateResponseDto response) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Map<String, Object> placeholders = mapper.convertValue(retrievedCaseDetails.getData(), Map.class);
        uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData caveatData = mapper.convertValue(placeholders,
                uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData.class);
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, null, retrievedCaseDetails.getId());
        return isSuccessfulPayment(response.getServiceRequestStatus())
                ? buildCaveatDataPaid(caveatDetails, response) : buildCaveatDataNotPaid(caveatDetails, response);
    }

    private CaveatData buildCaveatDataPaid(CaveatDetails caveatDetails, ServiceRequestUpdateResponseDto response) {
        CaveatCallbackRequest caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse =
                caveatNotificationService.solsCaveatRaise(caveatCallbackRequest);
        LocalDate appSubmittedDate =
                casePaymentBuilder.parseDate(caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
        LocalDate expiryDate =
                casePaymentBuilder.parseDate(caveatCallbackResponse.getCaveatData().getExpiryDate());
        List<CollectionMember<CasePayment>> allPayments = casePaymentBuilder.addPaymentFromServiceRequestResponse(
                caveatDetails.getData().getPayments(), response);
        return CaveatData.builder()
                .payments(allPayments)
                .paymentTaken(getPaymentTakenStatus(caveatDetails.getData().getPaymentTaken(),
                        response.getServiceRequestStatus()))
                .applicationSubmittedDate(appSubmittedDate)
                .notificationsGenerated(asNotificationsGenerated(caveatCallbackResponse.getCaveatData()
                        .getNotificationsGenerated()))
                .expiryDate(expiryDate)
                .build();
    }

    private CaveatData buildCaveatDataNotPaid(CaveatDetails caveatDetails, ServiceRequestUpdateResponseDto response) {
        List<CollectionMember<CasePayment>> allPayments = casePaymentBuilder.addPaymentFromServiceRequestResponse(
                caveatDetails.getData().getPayments(), response);
        return CaveatData.builder()
                .payments(allPayments)
                .paymentTaken(getPaymentTakenStatus(caveatDetails.getData().getPaymentTaken(),
                        response.getServiceRequestStatus()))
                .build();
    }

    private List<CollectionMember<ProbateDocument>> asNotificationsGenerated(
            List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<Document>> probateNotificationsGenerated) {
        List<CollectionMember<ProbateDocument>> probateDocsGenerated =
                new ArrayList<>();
        for (uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<Document> doc : probateNotificationsGenerated) {
            ProbateDocument probateDoc = doc.getValue().asProbateDocument();
            probateDocsGenerated.add(new CollectionMember<>(doc.getId(), probateDoc));
        }
        return probateDocsGenerated;
    }

    private EventId getEventIdByServiceRequestStatus(String serviceRequestStatus) {
        return isSuccessfulPayment(serviceRequestStatus)
                ? EventId.SERVICE_REQUEST_PAYMENT_SUCCESS : EventId.SERVICE_REQUEST_PAYMENT_FAILED;
    }

    private boolean isSuccessfulPayment(String serviceRequestStatus) {
        return SRP_STATUS_PAID.equals(serviceRequestStatus);
    }

    private String getPaymentTakenStatus(String paymentTaken, String serviceRequestStatus) {
        return YES.equals(paymentTaken) ? YES : isSuccessfulPayment(serviceRequestStatus) ? YES : NO;
    }
}
