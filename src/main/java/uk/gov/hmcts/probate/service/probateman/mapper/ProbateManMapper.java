package uk.gov.hmcts.probate.service.probateman.mapper;

import uk.gov.hmcts.probate.model.probateman.ProbateManModel;

public interface ProbateManMapper<F extends ProbateManModel, T> {

    T toCcdData(F probateManModel);
}
