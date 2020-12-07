'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#spouseOrPartner-${createGrantOfProbateConfig.page6_spouseOrPartnerNo}`);
        await I.click(`#childrenSurvived-${createGrantOfProbateConfig.page6_childrenSurvivedYes}`);
        await I.fillField('#childrenOverEighteenSurvived', createGrantOfProbateConfig.page6_childrenOverEighteenSurvived);
        await I.fillField('#childrenUnderEighteenSurvived', createGrantOfProbateConfig.page6_childrenUnderEighteenSurvived);
        await I.click(`#childrenDied-${createGrantOfProbateConfig.page6_childrenDiedYes}`);
        await I.fillField('#childrenDiedOverEighteen', createGrantOfProbateConfig.page6_childrenDiedOverEighteen);
        await I.fillField('#childrenDiedUnderEighteen', createGrantOfProbateConfig.page6_childrenDiedUnderEighteen);
        await I.click(`#grandChildrenSurvived-${createGrantOfProbateConfig.page6_grandChildrenSurvivedYes}`);
        await I.fillField('#grandChildrenSurvivedOverEighteen', createGrantOfProbateConfig.page6_grandChildrenSurvivedOverEighteen);
        await I.fillField('#grandChildrenSurvivedUnderEighteen', createGrantOfProbateConfig.page6_grandChildrenSurvivedUnderEighteen);

    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page6_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page6_list1_update_option);
        await I.click(commonConfig.continueButton);
        await I.waitForElement('#childrenDied-No');
        await I.seeElement('#childrenDied-Yes');
        await I.click(`#childrenDied-${createGrantOfProbateConfig.page6_childrenDiedNo}`);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
