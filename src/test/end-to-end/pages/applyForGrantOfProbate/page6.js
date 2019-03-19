'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(applyForGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
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
        I.fillField('#lodgedDate-day', applyForGrantOfProbateConfig.page1_lodgedDate_day_update);
        I.fillField('#lodgedDate-month', applyForGrantOfProbateConfig.page1_lodgedDate_month_update);
        I.fillField('#lodgedDate-year', applyForGrantOfProbateConfig.page1_lodgedDate_year_update);

        I.fillField('#numberOfCodicils', applyForGrantOfProbateConfig.page1_numberOfCodicils_update);

    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
