package uk.gov.hmcts.probate.service.caseaccess;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
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

@ContextConfiguration(classes = {AssignCaseAccessService.class})
@RunWith(SpringRunner.class)
public class AssignCaseAccessServiceTest {
    @MockBean
    private AssignCaseAccessClient assignCaseAccessClient;

    @Autowired
    private AssignCaseAccessService assignCaseAccessService;

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @MockBean
    private CcdDataStoreService ccdDataStoreService;

    @MockBean
    private IdamApi idamApi;

    @Test
    public void testAssignCaseAccess() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);

        when(this.idamApi.getUserDetails((String) any())).thenReturn(responseEntity);
        doNothing().when(this.ccdDataStoreService).removeCreatorRole((String) any(), (String) any());
        when(this.authTokenGenerator.generate()).thenReturn("Generate");
        doNothing().when(this.assignCaseAccessClient)
            .assignCaseAccess((String) any(), (String) any(), anyBoolean(),
                (uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest) any());
        this.assignCaseAccessService.assignCaseAccess("42", "ABC123");
        verify(this.idamApi).getUserDetails((String) any());
        verify(this.ccdDataStoreService).removeCreatorRole((String) any(), (String) any());
        verify(this.authTokenGenerator).generate();
        verify(this.assignCaseAccessClient).assignCaseAccess((String) any(), (String) any(), anyBoolean(),
            (uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest) any());
    }
}

