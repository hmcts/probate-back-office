package uk.gov.hmcts.probate.functional.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
@Slf4j
public class FunctionalTestUtils {
    public static final String TOKEN_PARM = "TOKEN_PARM";

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Value("${user.id.url}")
    private String userId;

    private String serviceToken;

    @Value("${probate.caseworker.email}")
    private String caseworkerEmail;

    @Value("${probate.caseworker.password}")
    private String caseworkerPassword;

    @Value("${probate.solicitor.email}")
    private String solicitorEmail;

    @Value("${probate.solicitor.password}")
    private String solicitorPassword;

    @Value("${probate.solicitor2.email}")
    private String solicitor2Email;

    @Value("${probate.solicitor2.password}")
    private String solicitor2Password;

    @Value("${evidence.management.url}")
    private String dmStoreUrl;

    @Value("${probate.scheduler.username}")
    private String schedulerEmail;

    @Value("${probate.scheduler.password}")
    private String schedulerPassword;

    @Value("${core_case_data.api.url}")
    private String coreCaseDataApiUrl;

    @Value("${user.auth.provider.oauth2.url}")
    private String authProviderUrl;

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
            final File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            final String fileContent = new String(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringFromFile(String fileName) {
        try {
            final File file = ResourceUtils.getFile(this.getClass().getResource(fileName));
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

    public Headers getHeaders(String userName, String password, Integer id) {
        final String authorizationToken = serviceAuthTokenGenerator.generateClientToken(userName, password);
        final String serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken),
            new Header("user-id", id.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, userId);
    }

    public Headers getHeadersWithUserId(String serviceToken, String userId) {
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization",
                serviceAuthTokenGenerator.generateAuthorisation(caseworkerEmail, caseworkerPassword)),
            new Header("user-id", userId));
    }

    public String downloadPdfAndParseToString(String documentUrl) {
        final Response document = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithUserId())
            .when().get(documentUrl.replace("http://dm-store:8080", dmStoreUrl)).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String downloadPdfAndParseToStringForScheduler(String documentUrl) {
        final String userId = getSchedulerCaseworkerUserId();
        final Response document = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithUserId(serviceToken, userId))
            .when().get(documentUrl.replace("http://dm-store:8080", dmStoreUrl)).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String downloadPdfAndParseToStringForHeaders(String documentUrl, Headers headers) {
        final Response document = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when().get(documentUrl.replace("http://dm-store:8080", dmStoreUrl)).andReturn();

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
        } finally {
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return parsedText;
    }

    public String getCaseworkerUserId() {
        return getUserId(caseworkerEmail, caseworkerPassword);
    }

    public String getSchedulerCaseworkerUserId() {
        return getUserId(schedulerEmail, schedulerPassword);
    }

    public String getUserId(String email, String password) {
        final String caseworkerToken = serviceAuthTokenGenerator.generateClientToken(email, password);
        final Headers headers = Headers.headers(
            new Header("Authorization", "Bearer " + caseworkerToken));

        final String userInfoUrl = authProviderUrl + "/details";
        final Response userResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when().get(userInfoUrl).andReturn();

        final JsonPath jsonPath = JsonPath.from(userResponse.getBody().asString());
        return jsonPath.get("id");
    }

    public Headers getHeadersWithCaseworkerUser() {
        final String authorizationToken = serviceAuthTokenGenerator.generateClientToken(caseworkerEmail,
                caseworkerPassword);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken));
    }

    public Headers getHeadersWithSolicitorUser() {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(solicitorEmail, solicitorPassword);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken));
    }

    public Headers getHeadersWithSolicitor2User() {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(solicitor2Email, solicitor2Password);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken));
    }

    public Headers getHeadersWithSchedulerCaseworkerUser() {
        final String authorizationToken = serviceAuthTokenGenerator.generateClientToken(schedulerEmail,
                schedulerPassword);
        final String id = getUserId(schedulerEmail, schedulerPassword);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken),
            new Header("user-id", id));
    }

    public String createCaseAsCaseworker(String caseJson, String eventId) {
        final String user = getCaseworkerUserId();
        final String ccdStartAsCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user
            + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/" + eventId + "/token";
        final Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdStartAsCaseworkerUrl).andReturn();
        final String token = startResponse.getBody().jsonPath().get("token");
        final String caseCreateJson = caseJson.replaceAll(TOKEN_PARM, token);
        final String submitForCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user
            + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases";
        Response submitResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .body(caseCreateJson)
            .when().post(submitForCaseworkerUrl).andReturn();
        return submitResponse.getBody().asString();
    }

    public String findCaseAsCaseworker(String caseId) {
        final String user = getCaseworkerUserId();
        final String ccdFindCaseUrl = coreCaseDataApiUrl + "/caseworkers/" + user
            + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId;
        final Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdFindCaseUrl).andReturn();
        return startResponse.getBody().asString();
    }

    public String updateCaseAsCaseworker(String caseJson, String eventId, String caseId) {
        final String updateToken = startUpdateCaseAsCaseworker(caseId, eventId);
        final String markAsReadyForExaminationUpdateJson = replaceAttribute(caseJson, TOKEN_PARM, updateToken);
        return continueUpdateCaseAsCaseworker(markAsReadyForExaminationUpdateJson, caseId);
    }

    public String startUpdateCaseAsCaseworker(String caseId, String eventId) {
        final String user = getCaseworkerUserId();
        final String ccdStartAsCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user
            + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId + "/event-triggers/" + eventId
            + "/token";
        final Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdStartAsCaseworkerUrl).andReturn();
        return startResponse.getBody().jsonPath().get("token");
    }

    public String continueUpdateCaseAsCaseworker(String caseJson, String caseId) {
        final String user = getCaseworkerUserId();
        final String submitForCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user
            + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId + "/events";
        final Response submitResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .body(caseJson)
            .when().post(submitForCaseworkerUrl).andReturn();
        return submitResponse.getBody().asString();
    }

    public String replaceAttribute(String json, String key, String value) {
        return json.replaceAll(key, value);
    }

    public String addAttribute(String json, String attributeKey, String attributeValue) {
        return json.replaceAll("\"applicationID\": \"603\",",
            "\"applicationID\": \"603\",\"" + attributeKey + "\": \"" + attributeValue + "\",");
    }

    public String convertToWelsh(LocalDate dateToConvert) {
        final String[] welshMonths = {"Ionawr","Chwefror","Mawrth","Ebrill","Mai","Mehefin","Gorffennaf","Awst","Medi",
            "Hydref", "Tachwedd","Rhagfyr"};

        if (dateToConvert == null) {
            return null;
        }
        final int day = dateToConvert.getDayOfMonth();
        final int year = dateToConvert.getYear();
        final int month = dateToConvert.getMonth().getValue();
        return String.join(" ", Integer.toString(day),  welshMonths[month - 1],
            Integer.toString(year));
    }
    
    public String formatDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        final DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        final DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            final Date date = originalFormat.parse(dateToConvert.toString());
            final String formattedDate = targetFormat.format(date);
            return addDayNumberSuffix(formattedDate);
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }
    
    private String addDayNumberSuffix(String formattedDate) {
        final int day = Integer.parseInt(formattedDate.substring(0, 2));
        switch (day) {
            case 3:
            case 23:
                return day + "rd " + formattedDate.substring(3);
            case 1:
            case 21:
            case 31:
                return day + "st " + formattedDate.substring(3);
            case 2:
            case 22:
                return day + "nd " + formattedDate.substring(3);
            default:
                return day + "th " + formattedDate.substring(3);
        }
    }
}
