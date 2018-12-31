package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

public interface ProbateManService {

    CaseDetails saveToCcd(Long id, ProbateManType probateManType);
}
