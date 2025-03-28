package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

import java.util.List;

import static java.util.Collections.emptyList;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LifeEventValidationRuleIT {

    @Autowired
    private LifeEventValidationRule lifeEventValidationRule;

    @Test
    void shouldThrowWhenNumberOfDeathRecordsMatchCollectionSize() {
        final CaseData caseData = CaseData.builder()
            .deathRecords(emptyList())
            .numberOfDeathRecords(1)
            .build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        Assertions.assertThatThrownBy(() -> {
            lifeEventValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Don't add or remove records here",
                    "Peidiwch ag ychwanegu na dileu cofnodion yma");
    }

    @Test
    void shouldThrowWhenRecordAddedInUI() {
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
            .hasMessage("Don't add or remove records here",
                    "Peidiwch ag ychwanegu na dileu cofnodion yma");
    }

    @Test
    void shouldThrowWhenMultipleRecordsSelected() {
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
            .hasMessage("Select one death record", "Dewiswch un cofnod marwolaeth");
    }

    @Test
    void shouldThrowWhenNoRecordsSelected() {
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
            .hasMessage("Select one death record","Dewiswch un cofnod marwolaeth");
    }


    @Test
    void shouldNotThrowWhenValid() {
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
