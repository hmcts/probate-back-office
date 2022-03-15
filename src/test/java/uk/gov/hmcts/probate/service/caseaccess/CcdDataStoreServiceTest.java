package uk.gov.hmcts.probate.service.caseaccess;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@ContextConfiguration(classes = {CcdDataStoreService.class})
@RunWith(SpringRunner.class)
public class CcdDataStoreServiceTest {
    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @MockBean
    private CaseRoleClient caseRoleClient;

    @Autowired
    private CcdDataStoreService ccdDataStoreService;

    @MockBean
    private IdamApi idamApi;

    @Test
    public void testRemoveCreatorRole() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);

        when(this.idamApi.getUserDetails((String) any())).thenReturn(responseEntity);
        doNothing().when(this.caseRoleClient)
            .removeCaseRoles((String) any(), (String) any(),
                (uk.gov.hmcts.probate.model.caseaccess.RemoveUserRolesRequest) any());
        when(this.authTokenGenerator.generate()).thenReturn("Generate");
        this.ccdDataStoreService.removeCreatorRole("42", "ABC123");
        verify(this.idamApi).getUserDetails((String) any());
        verify(this.caseRoleClient).removeCaseRoles((String) any(), (String) any(),
            (uk.gov.hmcts.probate.model.caseaccess.RemoveUserRolesRequest) any());
        verify(this.authTokenGenerator).generate();
    }
}

