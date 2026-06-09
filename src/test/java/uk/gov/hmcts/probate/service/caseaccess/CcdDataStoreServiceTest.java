package uk.gov.hmcts.probate.service.caseaccess;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CcdDataStoreService.class})
@ExtendWith(SpringExtension.class)
class CcdDataStoreServiceTest {
    @MockitoBean
    private AuthTokenGenerator authTokenGenerator;

    @MockitoBean
    private CaseRoleClient caseRoleClient;

    @Autowired
    private CcdDataStoreService ccdDataStoreService;

    @MockitoBean
    private IdamApi idamApi;

    @Test
    void testRemoveCreatorRole() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);

        when(this.idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        doNothing().when(this.caseRoleClient)
            .removeCaseRoles(anyString(), anyString(), any());
        when(this.authTokenGenerator.generate()).thenReturn("Generate");
        this.ccdDataStoreService.removeCreatorRole("42", "ABC123");
        verify(this.idamApi).getUserDetails(anyString());
        verify(this.caseRoleClient).removeCaseRoles(anyString(), anyString(), any());
        verify(this.authTokenGenerator).generate();
    }
}

