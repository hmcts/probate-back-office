'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page7_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#spouseOrPartner-${createGrantOfProbateConfig.page7_spouseOrPartnerNo}`);
        await I.click(`#childrenSurvived-${createGrantOfProbateConfig.page7_childrenSurvivedYes}`);
        await I.fillField('#childrenOverEighteenSurvived', createGrantOfProbateConfig.page7_childrenOverEighteenSurvived);
        await I.fillField('#childrenUnderEighteenSurvived', createGrantOfProbateConfig.page7_childrenUnderEighteenSurvived);
        await I.click(`#childrenDied-${createGrantOfProbateConfig.page7_childrenDiedYes}`);
        await I.fillField('#childrenDiedOverEighteen', createGrantOfProbateConfig.page7_childrenDiedOverEighteen);
        await I.fillField('#childrenDiedUnderEighteen', createGrantOfProbateConfig.page7_childrenDiedUnderEighteen);
        await I.click(`#grandChildrenSurvived-${createGrantOfProbateConfig.page7_grandChildrenSurvivedYes}`);
        await I.fillField('#grandChildrenSurvivedOverEighteen', createGrantOfProbateConfig.page7_grandChildrenSurvivedOverEighteen);
        await I.fillField('#grandChildrenSurvivedUnderEighteen', createGrantOfProbateConfig.page7_grandChildrenSurvivedUnderEighteen);

    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page7_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page7_list1_update_option);
        await I.click(commonConfig.continueButton);
        await I.waitForElement('#childrenDied-No');
        await I.seeElement('#childrenDied-Yes');
        await I.click(`#childrenDied-${createGrantOfProbateConfig.page7_childrenDiedNo}`);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
