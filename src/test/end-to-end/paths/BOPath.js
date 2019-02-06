'use strict';

//const taskListContent = require('app/resources/en/translation/tasklist');
const TestConfigurator = new (require('src/test/end-to-end/helpers/TestConfigurator'))();
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

Feature('Back Office - Will Lodgement for a Personal Applicant').retry(TestConfigurator.getRetryFeatures());

// eslint complains that the Before/After are not used but they are by codeceptjs
// so we have to tell eslint to not validate these
// eslint-disable-next-line no-undef
/*
Before(() => {
    TestConfigurator.getBefore();
});
*/

// eslint-disable-next-line no-undef
/*
After(() => {
    TestConfigurator.getAfter();
});
*/


Scenario(TestConfigurator.idamInUseText('Multiple Executors'), async function (I) {

    // IdAM
   I.authenticateWithIdamIfAvailable();
   I.selectNewCase();
   I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text, createCaseConfig.list3_text);
   I.enterWillLodgementPage1();
   I.enterWillLodgementPage2();
   I.enterWillLodgementPage3();
   I.checkMyAnswers();

   let url = await I.grabCurrentUrl();
   const caseRef = url.split('/').pop().match(/.{4}/g).join('-');
   I.seeCaseDetails(caseRef);
   /*
        I.enterGrossAndNet('205', '600000', '300000');
    } else {
        I.enterGrossAndNet('205', '500', '400');
    }

    I.selectDeceasedAlias('Yes');
    I.selectOtherNames('2');
    I.selectDeceasedMarriedAfterDateOnWill('No');
    I.selectWillCodicils('Yes');
    I.selectWillNoOfCodicils('3');

    // ExecutorsTask
    I.selectATask(taskListContent.taskNotStarted);
    I.enterApplicantName('Applicant First Name', 'Applicant Last Name');
    I.selectNameAsOnTheWill('No');
    I.enterApplicantAlias('Applicant Alias');
    I.enterApplicantAliasReason('aliasOther', 'Applicant_alias_reason');
    I.enterApplicantPhone();
    I.enterAddressManually();

    const totalExecutors = '1';
    I.enterTotalExecutors(totalExecutors);

    // Review and Confirm Task
    I.selectATask(taskListContent.taskNotStarted);
    I.seeSummaryPage('declaration');
    I.acceptDeclaration();

    // Extra Copies Task
    I.selectATask(taskListContent.taskNotStarted);

    if (TestConfigurator.getUseGovPay() === 'true') {
        I.enterUkCopies('5');
        I.selectOverseasAssets();
        I.enterOverseasCopies('7');
    } else {
        I.enterUkCopies('0');
        I.selectOverseasAssets();
        I.enterOverseasCopies('0');
    }

    I.seeCopiesSummary();

    // Payment Task
    I.selectATask(taskListContent.taskNotStarted);
    I.seePaymentBreakdownPage();

    if (TestConfigurator.getUseGovPay() === 'true') {
        I.seeGovUkPaymentPage();
        I.seeGovUkConfirmPage();
    }

    I.seePaymentStatusPage();

    // Send Documents Task
    I.seeDocumentsPage();

    // Thank You
    I.seeThankYouPage();
*/
});
