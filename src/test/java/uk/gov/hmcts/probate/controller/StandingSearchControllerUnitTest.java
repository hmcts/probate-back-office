package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.transformer.StandingSearchCallbackResponseTransformer;
import uk.gov.hmcts.probate.service.CcdSupplementaryDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StandingSearchControllerUnitTest {

    @Mock
    private StandingSearchCallbackResponseTransformer standingSearchCallbackResponseTransformer;
    @Mock
    private DocumentGeneratorService documentGeneratorService;

    @Mock
    private StandingSearchCallbackRequest callbackRequest;
    @Mock
    private StandingSearchDetails standingSearchDetails;
    @Mock
    private StandingSearchData standingSearchData;
    @Mock
    private StandingSearchCallbackResponse standingSearchCallbackResponse;

    @Mock
    CcdSupplementaryDataService ccdSupplementaryDataService;

    private StandingSearchController standingSearchController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(callbackRequest.getCaseDetails()).thenReturn(standingSearchDetails);
        when(standingSearchDetails.getData()).thenReturn(standingSearchData);

        standingSearchController = new StandingSearchController(standingSearchCallbackResponseTransformer,
                documentGeneratorService, ccdSupplementaryDataService);
    }

    @Test
    void shouldCreateStandingSearch() {
        when(standingSearchCallbackResponseTransformer.standingSearchCreated(callbackRequest))
                .thenReturn(standingSearchCallbackResponse);
        ResponseEntity<StandingSearchCallbackResponse> response =
                standingSearchController.createStandingSearch(callbackRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(standingSearchCallbackResponse, response.getBody());
    }

    @Test
    void shouldReturnErrorIfThereAreNoFilesInTheRequest() {
        when(standingSearchCallbackResponseTransformer.setupOriginalDocumentsForRemoval(callbackRequest))
                .thenReturn(standingSearchCallbackResponse);
        ResponseEntity<StandingSearchCallbackResponse> response =
                standingSearchController.setupForPermanentRemovalStandingSearch(callbackRequest);
        verify(standingSearchCallbackResponseTransformer, times(1)).setupOriginalDocumentsForRemoval(callbackRequest);
        assertEquals(standingSearchCallbackResponse, response.getBody());
    }

    @Test
    void shouldDeleteDocuments() {
        when(standingSearchCallbackResponseTransformer.transform(callbackRequest))
                .thenReturn(standingSearchCallbackResponse);

        ResponseEntity<StandingSearchCallbackResponse> response =
                standingSearchController.permanentlyDeleteRemovedStandingSearch(callbackRequest);
        verify(documentGeneratorService, times(1)).permanentlyDeleteRemovedDocumentsForStandingSearch(callbackRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(standingSearchCallbackResponse, response.getBody());
    }

}
