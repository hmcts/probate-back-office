package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CaveatorEmailAddressValidationRule implements CaveatEmailValidationRule{
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String EMAIL_NOT_FOUND_PA = "multipleEmailsNotProvidedPA";
    private static final String REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@([a-z0-9]{1,30}\\.){0,5}[a-z0-9](?:[a-z0-9-]{0,10}[a-z0-9])?";

    @Override
    public void validate(CaveatDetails caveatDetails) {
        String[] args = {caveatDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_PA, args, Locale.UK);

        if (caveatDetails.getData().getCaveatorEmailAddress() != null) {
          if(!caveatDetails.getData().getCaveatorEmailAddress().matches(REGEX)){
              throw new BusinessValidationException(userMessage,
                      "Caveator email does not meet the criteria for case id " + caveatDetails.getId());
          }
        }
    }
}
