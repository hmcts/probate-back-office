package uk.gov.hmcts.probate.service.caseaccess;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AssignCaseAccessService.class})
@ExtendWith(SpringExtension.class)
class AssignCaseAccessServiceTest {
    @MockitoBean
    private AssignCaseAccessClient assignCaseAccessClient;

    @Autowired
    private AssignCaseAccessService assignCaseAccessService;

    @MockitoBean
    private AuthTokenGenerator authTokenGenerator;

    @MockitoBean
    private CcdDataStoreService ccdDataStoreService;

    @MockitoBean
    private IdamApi idamApi;

    @Test
    void testAssignCaseAccess() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);

        when(idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        doNothing().when(ccdDataStoreService).removeCreatorRole(anyString(), anyString());
        when(authTokenGenerator.generate()).thenReturn("Generate");
        doNothing().when(assignCaseAccessClient)
            .assignCaseAccess(anyString(), anyString(), anyBoolean(), any());
        assignCaseAccessService.assignCaseAccess("42", "ABC123", "GrantOfRepersentation");
        verify(idamApi).getUserDetails(anyString());
        verify(ccdDataStoreService).removeCreatorRole(anyString(), anyString());
        verify(authTokenGenerator).generate();
    }

    @Test
    void testAssignCaseAccessThrowsFeignException() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);

        when(idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        doNothing().when(ccdDataStoreService).removeCreatorRole(anyString(), anyString());
        when(authTokenGenerator.generate()).thenReturn("Generate");
        doThrow(FeignException.class).when(assignCaseAccessClient)
                .assignCaseAccess(anyString(), anyString(), anyBoolean(), any());
        assignCaseAccessService.assignCaseAccess("ABC123", "42", "GrantOfRepersentation");

        verify(idamApi).getUserDetails(anyString());
        verify(authTokenGenerator).generate();
    }
}

