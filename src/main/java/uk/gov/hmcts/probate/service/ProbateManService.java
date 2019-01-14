package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

public interface ProbateManService {

    ProbateManModel getProbateManModel(Long id, ProbateManType probateManType);

    CaseDetails saveToCcd(Long id, ProbateManType probateManType);
}
