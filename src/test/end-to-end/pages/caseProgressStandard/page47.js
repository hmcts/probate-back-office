//boExaminationChecklistQ1-Yes
'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case Mark as ready to issue
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    await I.waitForElement({css: '#boExaminationChecklistQ1-Yes'});
    await I.click({css: '#boExaminationChecklistQ1-Yes'});
    await I.waitForElement({css: '#boExaminationChecklistQ2-Yes'});
    await I.click({css: '#boExaminationChecklistQ2-Yes'});
    await I.waitForElement({css: '#boExaminationChecklistRequestQA-No'});
    await I.click({css: '#boExaminationChecklistRequestQA-No'});

    await I.waitForNavigationToComplete(commonConfig.goButton);  
};
