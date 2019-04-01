'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page5_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#willExists-${createGrantOfProbateConfig.page5_willExistsYes}`);
        I.click(`#willDatedBeforeApril-${createGrantOfProbateConfig.page5_willDatedBeforeAprilYes}`);
        I.click(`#willAccessOriginal-${createGrantOfProbateConfig.page5_willAccessOriginalYes}`);
        I.click(`#willHasCodicils-${createGrantOfProbateConfig.page5_willHasCodicilsYes}`);
        I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page5_willNumberOfCodicils);
        I.click(`#willsOutsideOfUK-${createGrantOfProbateConfig.page5_willsOutsideOfUKYes}`);
        I.click(`#deceasedEnterMarriageOrCP-${createGrantOfProbateConfig.page5_deceasedEnterMarriageOrCPYes}`);

        I.fillField('#dateOfMarriageOrCP-day', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_day);
        I.fillField('#dateOfMarriageOrCP-month', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_month);
        I.fillField('#dateOfMarriageOrCP-year', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_year);

        I.fillField('#dateOfDivorcedCPJudicially-day', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_day);
        I.fillField('#dateOfDivorcedCPJudicially-month', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_month);
        I.fillField('#dateOfDivorcedCPJudicially-year', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_year);

        I.fillField('#courtOfDecree', createGrantOfProbateConfig.page5_courtOfDecree);
        I.click(`#willGiftUnderEighteen-${createGrantOfProbateConfig.page5_willGiftUnderEighteenYes}`);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page5_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page5_list1_update_option);
        I.click(commonConfig.continueButton);

        I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page5_willNumberOfCodicils_update);

    }

    I.click(commonConfig.continueButton);
};
