'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('02 BO Case Progress E2E - stop/escalate/issue', async function (I) {
        // IDAM
    try {
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        
        await I.caseProgressStopEscalateIssuePage1();
        await I.caseProgressStopEscalateIssuePage2();
        await I.caseProgressStopEscalateIssuePage3();
        await I.caseProgressStopEscalateIssuePage4();        
        await I.caseProgressStopEscalateIssuePage5();        
        await I.caseProgressStopEscalateIssuePage6();        
        await I.caseProgressStopEscalateIssuePage7();                
        await I.caseProgressStopEscalateIssuePage8();                
        await I.caseProgressStopEscalateIssuePage9();       
        await I.caseProgressStopEscalateIssuePage10();                    
        await I.caseProgressStopEscalateIssuePage11();                    
        await I.caseProgressStopEscalateIssuePage12();                    
        await I.caseProgressStopEscalateIssuePage13();                    
        await I.caseProgressStopEscalateIssuePage14();                    
        await I.caseProgressStopEscalateIssuePage15();   
        await I.caseProgressStopEscalateIssuePage16();   
        await I.caseProgressStopEscalateIssuePage17();   
        await I.caseProgressStopEscalateIssuePage18();   
        await I.caseProgressStopEscalateIssuePage19();   
        await I.caseProgressStopEscalateIssuePage20();   
        await I.caseProgressStopEscalateIssuePage21();   
        const caseRef = await I.caseProgressStopEscalateIssuePage22();   
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStopEscalateIssuePage23(caseRef);   
        await I.caseProgressStopEscalateIssuePage24();   
        await I.caseProgressStopEscalateIssuePage25();   
        await I.caseProgressStopEscalateIssuePage26();   
        await I.caseProgressStopEscalateIssuePage27();   
        await I.caseProgressStopEscalateIssuePage28();   
        await I.caseProgressStopEscalateIssuePage29();           
        await I.caseProgressStopEscalateIssuePage30();   
        
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStopEscalateIssuePage31(caseRef);   
        await I.caseProgressStopEscalateIssuePage32();   


        // log back in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        // set as escalated
        await I.caseProgressStopEscalateIssuePage33(caseRef);   
        await I.caseProgressStopEscalateIssuePage34();   
        await I.caseProgressStopEscalateIssuePage35();   
        await I.caseProgressStopEscalateIssuePage36();   
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStopEscalateIssuePage37(caseRef);   
        await I.caseProgressStopEscalateIssuePage38();   
        // log back in as case worker and set to Find Matches (Issue grant)
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStopEscalateIssuePage39(caseRef);   
        await I.caseProgressStopEscalateIssuePage40();   
        await I.caseProgressStopEscalateIssuePage41();   
        await I.caseProgressStopEscalateIssuePage42();   
        await I.caseProgressStopEscalateIssuePage43();   
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStopEscalateIssuePage44(caseRef);   
        await I.caseProgressStopEscalateIssuePage45();   
        // log back in as case worker and set to Issue grant
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressStopEscalateIssuePage46(caseRef);   
        await I.caseProgressStopEscalateIssuePage47();   
        await I.caseProgressStopEscalateIssuePage48();   
        await I.caseProgressStopEscalateIssuePage49();   
        await I.caseProgressStopEscalateIssuePage50();   

        // log back in as solicitor & check all sections completed
        await I.authenticateWithIdamIfAvailable(true, true); 
        await I.caseProgressStopEscalateIssuePage51(caseRef);   
        await I.caseProgressStopEscalateIssuePage52();   
        
        console.info('02 BO Case Progress E2E - stop/escalate/issue: complete');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }    

}).retry(0); //testConfig.TestRetryScenarios);