package uk.gov.hmcts.probate.model.exceptionrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ExceptionRecordOCRFields {

    // Caveats
    private final String caveatorForenames;
    private final String caveatorSurnames;
    private final String caveatorEmailAddress;
    private final String caveatorAddressLine1;
    private final String caveatorAddressLine2;
    private final String caveatorAddressTown;
    private final String caveatorAddressCounty;
    private final String caveatorAddressPostCode;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedAnyOtherNames;
    private final String deceasedAddressLine1;
    private final String deceasedAddressLine2;
    private final String deceasedAddressTown;
    private final String deceasedAddressCounty;
    private final String deceasedAddressPostCode;

    // PA1A and PA1P
    private final String ihtFormCompletedOnline;
    private final String ihtReferenceNumber;
    private final String ihtGrossValue;
    private final String ihtNetValue;
    private final String ihtFormId;
}
