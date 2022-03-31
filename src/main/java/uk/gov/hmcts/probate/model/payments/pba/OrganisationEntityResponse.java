package uk.gov.hmcts.probate.model.payments.pba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationEntityResponse {

    @JsonProperty("organisationIdentifier")
    private String organisationIdentifier;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "sraId")
    private String sraId;

    @JsonProperty(value = "sraRegulated")
    private boolean sraRegulated;

    @JsonProperty(value = "companyNumber")
    private String companyNumber;

    @JsonProperty(value = "companyUrl")
    private String companyUrl;

    @JsonProperty(value = "paymentAccount")
    private List<String> paymentAccount;

    @JsonProperty(value = "superUser")
    private SuperUserResponse superUser;

    @JsonProperty(value = "contactInformation")
    private List<ContactInformationResponse> contactInformation;

    @JsonProperty(value = "pendingPaymentAccount")
    private List<String> pendingPaymentAccount = new ArrayList<String>();

    @JsonProperty(value = "dateReceived")
    @DateTimeFormat
    private LocalDateTime dateReceived;

    @JsonProperty(value = "dateApproved")
    @DateTimeFormat
    @JsonInclude(ALWAYS)
    private LocalDateTime dateApproved = null;

}
