package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

import static java.util.Collections.emptyList;

public class LifeEventValidationRuleTest {

    private LifeEventValidationRule lifeEventValidationRule = new LifeEventValidationRule();
    
    @Test
    public void shouldThrowWhenNumberOfDeathRecordsMatchCollectionSize() {
        final CaseData caseData = CaseData.builder()
            .deathRecords(emptyList())
            .numberOfDeathRecords(1)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        Assertions.assertThatThrownBy(() -> {
            lifeEventValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Don't add or remove records here");
    }
    
    @Test
    public void shouldThrowWhenRecordAddedInUI() {
        DeathRecord deathRecord = DeathRecord.builder().build();
        CollectionMember collectionMember = new CollectionMember(null, deathRecord);
        final CaseData caseData = CaseData.builder()
            .deathRecords(List.of(collectionMember))
            .numberOfDeathRecords(1)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        Assertions.assertThatThrownBy(() -> {
            lifeEventValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Don't add or remove records here");
    }
    
    @Test
    public void shouldThrowWhenMultipleRecordsSelected() {
        DeathRecord deathRecord1 = DeathRecord
            .builder()
            .systemNumber(1)
            .valid("Yes")
            .build();
        DeathRecord deathRecord2 = DeathRecord
            .builder()
            .systemNumber(2)
            .valid("Yes")
            .build();
        CollectionMember collectionMember1 = new CollectionMember(null, deathRecord1);
        CollectionMember collectionMember2 = new CollectionMember(null, deathRecord2);
        final CaseData caseData = CaseData.builder()
            .deathRecords(List.of(collectionMember1, collectionMember2))
            .numberOfDeathRecords(2)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        Assertions.assertThatThrownBy(() -> {
            lifeEventValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Select one death record");
    }

    @Test
    public void shouldThrowWhenNoRecordsSelected() {
        DeathRecord deathRecord1 = DeathRecord
            .builder()
            .systemNumber(1)
            .valid("No")
            .build();
        DeathRecord deathRecord2 = DeathRecord
            .builder()
            .systemNumber(2)
            .valid("No")
            .build();
        CollectionMember collectionMember1 = new CollectionMember(null, deathRecord1);
        CollectionMember collectionMember2 = new CollectionMember(null, deathRecord2);
        final CaseData caseData = CaseData.builder()
            .deathRecords(List.of(collectionMember1, collectionMember2))
            .numberOfDeathRecords(2)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        Assertions.assertThatThrownBy(() -> {
            lifeEventValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Select one death record");
    }


    @Test
    public void shouldNotThrowWhenValid() {
        DeathRecord deathRecord1 = DeathRecord
            .builder()
            .systemNumber(1)
            .valid("Yes")
            .build();

        CollectionMember collectionMember1 = new CollectionMember(null, deathRecord1);
        final CaseData caseData = CaseData.builder()
            .deathRecords(List.of(collectionMember1))
            .numberOfDeathRecords(1)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
            
        lifeEventValidationRule.validate(caseDetails);
  
    }
}
