'use strict';

// For further improvement of test code,
// move common code for pages 1-n into a smaller number of reusable files
// and rename files as to what they do - enter deceased details etc

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Case Progress E2E - standard path', async function (I) {
    // IDAM
    try {
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.caseProgressStandardPage1();
        await I.caseProgressStandardPage2();
        await I.caseProgressStandardPage3();
        await I.caseProgressStandardPage4();
        await I.caseProgressStandardPage5();
        await I.caseProgressStandardPage6();
        await I.caseProgressStandardPage7();
        await I.caseProgressStandardPage8();
        await I.caseProgressStandardPage9();
        await I.caseProgressStandardPage10();
        await I.caseProgressStandardPage11();
        await I.caseProgressStandardPage12();
        await I.caseProgressStandardPage13();
        await I.caseProgressStandardPage14();
        await I.caseProgressStandardPage15();
        await I.caseProgressStandardPage16();
        await I.caseProgressStandardPage17();
        await I.caseProgressStandardPage18();
        await I.caseProgressStandardPage19();
        await I.caseProgressStandardPage20();
        await I.caseProgressStandardPage21();
        const caseRef = await I.caseProgressStandardPage22();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage23(caseRef);
        await I.caseProgressStandardPage24();
        await I.caseProgressStandardPage25();
        await I.caseProgressStandardPage26();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage27(caseRef);
        await I.caseProgressStandardPage28();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage29(caseRef); 
        await I.caseProgressStandardPage30(); // mark as ready for examination
        await I.caseProgressStandardPage31(); 
        await I.caseProgressStandardPage32(); 

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage33(caseRef);
        await I.caseProgressStandardPage34();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage35(caseRef);
        await I.caseProgressStandardPage36();
        await I.caseProgressStandardPage37();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage38(caseRef);
        await I.caseProgressStandardPage39();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage40(caseRef);
        await I.caseProgressStandardPage41();
        await I.caseProgressStandardPage42();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage43(caseRef);
        await I.caseProgressStandardPage44();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage45(caseRef);
        await I.caseProgressStandardPage46();
        await I.caseProgressStandardPage47();
        await I.caseProgressStandardPage48();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage49(caseRef);
        await I.caseProgressStandardPage50();

        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage51(caseRef);
        await I.caseProgressStandardPage52();
        await I.caseProgressStandardPage53();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage54(caseRef);
        await I.caseProgressStandardPage55();


        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStandardPage56(caseRef);
        await I.caseProgressStandardPage57();
        await I.caseProgressStandardPage58();
        await I.caseProgressStandardPage59();

        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStandardPage60(caseRef);
        await I.caseProgressStandardPage61();

        console.info('01 BO Case Progress E2E - standard: complete');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }    

}).retry(0); //testConfig.TestRetryScenarios);