'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#spouseOrPartner-${applyForGrantOfProbateConfig.page6_spouseOrPartnerNo}`);
        I.click(`#childrenSurvived-${applyForGrantOfProbateConfig.page6_childrenSurvivedYes}`);
        I.fillField('#childrenOverEighteenSurvived', applyForGrantOfProbateConfig.page6_childrenOverEighteenSurvived);
        I.fillField('#childrenUnderEighteenSurvived', applyForGrantOfProbateConfig.page6_childrenUnderEighteenSurvived);
        I.click(`#childrenDied-${applyForGrantOfProbateConfig.page6_childrenDiedYes}`);
        I.fillField('#childrenDiedOverEighteen', applyForGrantOfProbateConfig.page6_childrenDiedOverEighteen);
        I.fillField('#childrenDiedUnderEighteen', applyForGrantOfProbateConfig.page6_childrenDiedUnderEighteen);
        I.click(`#grandChildrenSurvived-${applyForGrantOfProbateConfig.page6_grandChildrenSurvivedYes}`);
        I.fillField('#grandChildrenSurvivedOverEighteen', applyForGrantOfProbateConfig.page6_grandChildrenSurvivedOverEighteen);
        I.fillField('#grandChildrenSurvivedUnderEighteen', applyForGrantOfProbateConfig.page6_grandChildrenSurvivedUnderEighteen);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page6_list1_update_option);
        I.click(commonConfig.continueButton);
        I.click(`#childrenDied-${applyForGrantOfProbateConfig.page6_childrenDiedNo}`);

    }

    I.click(commonConfig.continueButton);
};
