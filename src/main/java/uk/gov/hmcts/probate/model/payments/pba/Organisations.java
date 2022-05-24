package uk.gov.hmcts.probate.model.payments.pba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organisations {
    private List<OrganisationEntityResponse> organisations;
}
