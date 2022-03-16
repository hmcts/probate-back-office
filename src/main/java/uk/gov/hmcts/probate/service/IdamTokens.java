package uk.gov.hmcts.probate.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdamTokens {
    String idamOauth2Token;
    String serviceAuthorization;
    final String userId;
    final String email;
    final List<String> roles;
}
