'use strict';

const testConfig = require('src/test/config.js');
const caseSearchConfig = require('./caseSearchConfig.json');

module.exports = function (casetype) {

    const I = this;
    I.waitForText(caseSearchConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.click(caseSearchConfig.searchBtn);
//    I.waitForText(caseSearchConfig.waitForText2, testConfig.TestTimeToWaitForText);
    I.wait(10);
    I.selectOption(caseSearchConfig.caseType, casetype);
//    I.fillField(caseSearchConfig.caseReference, "Test123");
//    I.selectOption(caseSearchConfig.applicationType, "Personal");
//    I.selectOption(caseSearchConfig.registryLocation, "Bristol");
    I.fillField(caseSearchConfig.deceasedForenames, "ROBERT SMITH"); //"ALBERT");
    I.fillField(caseSearchConfig.deceasedSurname, "WILIE"); //"MOORE");
//    I.fillField(caseSearchConfig.deceasedDayOD, "01");
//    I.fillField(caseSearchConfig.deceasedMonthOD, "01");
//    I.fillField(caseSearchConfig.deceasedYearOD, "2017");

    if(casetype == "Grant of representation"){
//        I.fillField(caseSearchConfig.appCompletedDay, "01");
//        I.fillField(caseSearchConfig.appCompletedMonth, "01");
//        I.fillField(caseSearchConfig.appCompletedYear, "2019");
//        I.fillField(caseSearchConfig.grantIssuedDay, "01");
//        I.fillField(caseSearchConfig.grantIssuedMonth, "01");
//        I.fillField(caseSearchConfig.grantIssuedYear, "2019");
//        I.selectOption(caseSearchConfig.state, "Case imported");
//        I.selectOption(caseSearchConfig.casePrinted, "Yes");
//        I.fillField(caseSearchConfig.deceasedDayOB, "01");
//        I.fillField(caseSearchConfig.deceasedMonthOB, "01");
//        I.fillField(caseSearchConfig.deceasedYearOB, "1930");
//        I.click("#evidenceHandled-Yes");
        I.selectOption(caseSearchConfig.caseType2, "Grant of Probate");
    }
    else if(casetype == "Caveat"){
        I.selectOption(caseSearchConfig.state, "Caveat imported");
        //I.fillField(caseSearchConfig.caveatorForenames, "ROBERT SMITH");
        //I.fillField(caseSearchConfig.caveatorSurname, "WILIE");
        //I.fillField(caseSearchConfig.expiryDateDay, "1");
        //I.fillField(caseSearchConfig.expiryDateMonth, "1");
        //I.fillField(caseSearchConfig.expiryDateYear, "2019");
    }
    else if(casetype == "Standing search"){

        I.selectOption(caseSearchConfig.state, "Standing search imported");
        I.fillField(caseSearchConfig.applicantForenames, "User");
        I.fillField(caseSearchConfig.applicantSurname, "Test");
        I.fillField(caseSearchConfig.expiryDateDay, "1");
        I.fillField(caseSearchConfig.expiryDateMonth, "1");
        I.fillField(caseSearchConfig.expiryDateYear, "2019");
    }
    else if(casetype == "Will lodgement"){
        I.selectOption(caseSearchConfig.lodgementType, "Safe custody");
        I.selectOption(caseSearchConfig.state, "Will imported");
        I.fillField(caseSearchConfig.deceasedDayOB, "01");
        I.fillField(caseSearchConfig.deceasedMonthOB, "01");
        I.fillField(caseSearchConfig.deceasedYearOB, "1930");
        I.fillField(caseSearchConfig.executorForenames, "Exec");
        I.fillField(caseSearchConfig.executorSurname, "User");
    }

    I.wait(20);
    I.click(caseSearchConfig.continueBtn);
    I.wait(5);
};