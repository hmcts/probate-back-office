'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgress/caseProgressConfig');


const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');
const markForIssueConfig = require('src/test/end-to-end/pages/markForIssue/markForIssueConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const examChecklistTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/examChecklistTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/paymentDetailsTabConfig');

const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsUpdateTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');


Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Case Progress E2E', async function (I) {
    // IDAM
    try {
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.caseProgressPage1();
        await I.caseProgressPage2();
        await I.caseProgressPage3();
        await I.caseProgressPage4();        
        await I.caseProgressPage5();        
        await I.caseProgressPage6();        
        await I.caseProgressPage7();                
        await I.caseProgressPage8();                
        await I.caseProgressPage9();       
        await I.caseProgressPage10();                    
        await I.caseProgressPage11();                    
        await I.caseProgressPage12();                    
        await I.caseProgressPage13();                    
        await I.caseProgressPage14();                    
        await I.caseProgressPage15();   
        await I.caseProgressPage16();   
        await I.caseProgressPage17();   
        await I.caseProgressPage18();   
        await I.caseProgressPage19();   
        await I.caseProgressPage20();   
        await I.caseProgressPage21();   
        const caseRef = await I.caseProgressPage22();   
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressPage23(caseRef);   
        await I.caseProgressPage24();   
        await I.caseProgressPage25();   
        await I.caseProgressPage26();   
        await I.caseProgressPage27();   
        await I.caseProgressPage28();   
        await I.caseProgressPage29();   
        await I.caseProgressPage30();   
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressPage31(caseRef);   
        await I.caseProgressPage32();   
        // log back in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        // set as escalated
        await I.caseProgressPage33(caseRef);   
        await I.caseProgressPage34();   
        await I.caseProgressPage35();   
        await I.caseProgressPage36();   
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressPage37(caseRef);   
        await I.caseProgressPage38();   
        // log back in as case worker and set to Find Matches (Issue grant)
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressPage39(caseRef);   
        await I.caseProgressPage40();   
        await I.caseProgressPage41();   
        await I.caseProgressPage42();   
        await I.caseProgressPage43();   
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressPage44(caseRef);   
        await I.caseProgressPage45();   
        // log back in as case worker and set to Issue grant
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressPage46(caseRef);   
        await I.caseProgressPage47();   
        await I.caseProgressPage48();   
        await I.caseProgressPage49();   
        await I.caseProgressPage50();   

        // log back in as solicitor & check all sections completed
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressPage51(caseRef);   
        await I.caseProgressPage52();   

        console.info('01 BO Case Progress E2E complete');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }

}).retry(0); //testConfig.TestRetryScenarios);