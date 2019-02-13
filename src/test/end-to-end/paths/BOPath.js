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
    I.enterWillLodgementPage1('create');
    I.enterWillLodgementPage2('create');
    I.enterWillLodgementPage3('create');
    I.checkMyAnswers();

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');
    I.seeCaseDetails(caseRef, '1');

    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef);
    I.seeCaseDetails(caseRef, '2');

    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();
    I.seeCaseDetails(caseRef, '3');

    I.enterComment(caseRef);
    I.seeCaseDetails(caseRef, '4');

    I.generateDepositReceipt(caseRef, '5');
    I.seeCaseDetails(caseRef, '5');

    I.selectMatchedCases(caseRef);
    I.enterMatchedCasesComment(caseRef);
    I.seeCaseDetails(caseRef, '6');

});
