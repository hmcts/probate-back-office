package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;


@ExtendWith(SpringExtension.class)
class CaveatExpiryTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private CaveatExpiryService caveatExpiryService;

    @InjectMocks
    private CaveatExpiryTask caveatExpiryTask;
    private static final String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
    private String adhocDate = "2022-09-05";

    @Test
    void shouldExpireCaveatCases() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform caveat expiry finished");
        caveatExpiryTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform caveat expiry finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date);
        verify(caveatExpiryService).expireCaveats(date);
    }

    @Test
    void shouldExpireCaveatCasesForAdhocDate() {
        caveatExpiryTask.adHocJobDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform caveat expiry finished");
        caveatExpiryTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform caveat expiry finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate);
        verify(caveatExpiryService).expireCaveats(adhocDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForExpireCaveatCasesWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date);
        caveatExpiryTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(caveatExpiryService);
    }

    @Test
    void shouldThrowExceptionForExpireCaveatCases() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date);
        caveatExpiryTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(caveatExpiryService);
    }
}
