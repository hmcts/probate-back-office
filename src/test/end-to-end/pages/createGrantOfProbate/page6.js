'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#spouseOrPartner-${createGrantOfProbateConfig.page6_spouseOrPartnerNo}`);
        I.click(`#childrenSurvived-${createGrantOfProbateConfig.page6_childrenSurvivedYes}`);
        I.fillField('#childrenOverEighteenSurvived', createGrantOfProbateConfig.page6_childrenOverEighteenSurvived);
        I.fillField('#childrenUnderEighteenSurvived', createGrantOfProbateConfig.page6_childrenUnderEighteenSurvived);
        I.click(`#childrenDied-${createGrantOfProbateConfig.page6_childrenDiedYes}`);
        I.fillField('#childrenDiedOverEighteen', createGrantOfProbateConfig.page6_childrenDiedOverEighteen);
        I.fillField('#childrenDiedUnderEighteen', createGrantOfProbateConfig.page6_childrenDiedUnderEighteen);
        I.click(`#grandChildrenSurvived-${createGrantOfProbateConfig.page6_grandChildrenSurvivedYes}`);
        I.fillField('#grandChildrenSurvivedOverEighteen', createGrantOfProbateConfig.page6_grandChildrenSurvivedOverEighteen);
        I.fillField('#grandChildrenSurvivedUnderEighteen', createGrantOfProbateConfig.page6_grandChildrenSurvivedUnderEighteen);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page6_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page6_list1_update_option);
        I.click(commonConfig.continueButton);
        I.seeElement('#childrenDied-No');
        I.seeElement('#childrenDied-Yes');
        I.click(`#childrenDied-${createGrantOfProbateConfig.page6_childrenDiedNo}`);

    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
