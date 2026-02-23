package uk.gov.hmcts.probate.service.wa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WorkAllocationToggleService {

    @Value("${probate.wa.enabled}")
    private boolean probateWAEnabled;

    public boolean isProbateWAEnabledToggleOn() {
        return probateWAEnabled;
    }

}
