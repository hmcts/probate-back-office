package uk.gov.hmcts.probate.functional.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.functional.SolCCDServiceAuthTokenGenerator;
import uk.gov.hmcts.probate.functional.TestContextConfiguration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
public class FunctionalTestUtils {

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Value("${user.id.url}")
    private String userId;

    private String serviceToken;

    @Value("${probate.caseworker.email}")
    private String caseworkerEmail;

    @Value("${probate.caseworker.password}")
    private String caseworkerPassword;

    @PostConstruct
    public void init() {
        serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        if (userId == null || userId.isEmpty()) {
            serviceAuthTokenGenerator.createNewUser();
            userId = serviceAuthTokenGenerator.getUserId();
        }
    }

    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource(fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Headers getHeaders() {
        return getHeaders(serviceToken);
    }

    public Headers getHeaders(String serviceToken) {
        return Headers.headers(
                new Header("ServiceAuthorization", serviceToken),
                new Header("Content-Type", ContentType.JSON.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, userId);
    }

    private Headers getHeadersWithUserId(String serviceToken, String userId) {
        return Headers.headers(
                new Header("ServiceAuthorization", serviceToken),
                new Header("Content-Type", ContentType.JSON.toString()),
                new Header("Authorization", serviceAuthTokenGenerator.generateAuthorisation(caseworkerEmail, caseworkerPassword)),
                new Header("user-id", userId));
    }

    public String downloadPdfAndParseToString(String documentUrl) {
        Response document = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(getHeadersWithUserId())
                .when().get(documentUrl).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String parsePDFToString(InputStream inputStream) {

        PDFParser parser;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        String parsedText = "";

        try {
            parser = new PDFParser(inputStream);
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }

        }
        return parsedText;
    }

    public Headers getHeaders(String userName, String password, Integer id) {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(userName, password);
        String serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken),
            new Header("user-id", id.toString()));
    }

}
