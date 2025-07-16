package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SentEmailPersonalisationServiceIT {

    @Autowired
    private SentEmailPersonalisationService sentEmailPersonalisationService;

    @MockitoBean
    private PDFManagementService pdfManagementService;

    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private CaveatQueryService caveatQueryServiceMock;

    @MockitoBean
    private SendEmailResponse sendEmailResponse;

    private SentEmail sentEmail;

    private static final String PERSONALISATION_SENT_EMAIL_BODY = "body";
    private static final String PERSONALISATION_SENT_EMAIL_TO = "to";
    private static final String PERSONALISATION_SENT_EMAIL_FROM = "from";
    private static final String PERSONALISATION_SENT_EMAIL_SUBJECT = "subject";
    private static final String PERSONALISATION_SENT_EMAIL_SENT_ON = "sentOn";

    private static final String PERSONALISATION_SENT_EMAIL_BODY_RESPONSE = "bodyResponse";
    private static final String PERSONALISATION_SENT_EMAIL_TO_RESPONSE = "toResponse";
    private static final String PERSONALISATION_SENT_EMAIL_FROM_RESPONSE = "fromResponse";
    private static final String PERSONALISATION_SENT_EMAIL_SUBJECT_RESPONSE = "subjectResponse";
    private static final String PERSONALISATION_SENT_EMAIL_SENT_ON_RESPONSE = "sentOnResponse";

    @BeforeEach
    public void setUp() {

        sentEmail = SentEmail.builder()
                .sentOn(PERSONALISATION_SENT_EMAIL_SENT_ON_RESPONSE)
                .body(PERSONALISATION_SENT_EMAIL_BODY_RESPONSE)
                .from(PERSONALISATION_SENT_EMAIL_FROM_RESPONSE)
                .subject(PERSONALISATION_SENT_EMAIL_SUBJECT_RESPONSE)
                .to(PERSONALISATION_SENT_EMAIL_TO_RESPONSE)
                .build();
    }

    @Test
    void getPersonalisationContentIsOk() {
        Map<String, Object> response = sentEmailPersonalisationService.getPersonalisation(sentEmail);

        assertEquals(PERSONALISATION_SENT_EMAIL_SENT_ON_RESPONSE, response.get(PERSONALISATION_SENT_EMAIL_SENT_ON));
        assertEquals(PERSONALISATION_SENT_EMAIL_BODY_RESPONSE, response.get(PERSONALISATION_SENT_EMAIL_BODY));
        assertEquals(PERSONALISATION_SENT_EMAIL_FROM_RESPONSE, response.get(PERSONALISATION_SENT_EMAIL_FROM));
        assertEquals(PERSONALISATION_SENT_EMAIL_SUBJECT_RESPONSE, response.get(PERSONALISATION_SENT_EMAIL_SUBJECT));
        assertEquals(PERSONALISATION_SENT_EMAIL_TO_RESPONSE, response.get(PERSONALISATION_SENT_EMAIL_TO));
    }
}
