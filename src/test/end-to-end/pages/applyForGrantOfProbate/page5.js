'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page5_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#willExists-${applyForGrantOfProbateConfig.page5_willExistsYes}`);
        I.click(`#willDatedBeforeApril-${applyForGrantOfProbateConfig.page5_willDatedBeforeAprilYes}`);
        I.click(`#willAccessOriginal-${applyForGrantOfProbateConfig.page5_willAccessOriginalYes}`);
        I.click(`#willHasCodicils-${applyForGrantOfProbateConfig.page5_willHasCodicilsYes}`);
        I.fillField('#willNumberOfCodicils', applyForGrantOfProbateConfig.page5_willNumberOfCodicils);
        I.click(`#willsOutsideOfUK-${applyForGrantOfProbateConfig.page5_willsOutsideOfUKYes}`);
        I.click(`#deceasedEnterMarriageOrCP-${applyForGrantOfProbateConfig.page5_deceasedEnterMarriageOrCPYes}`);

        I.fillField('#dateOfMarriageOrCP-day', applyForGrantOfProbateConfig.page5_dateOfMarriageOrCP_day);
        I.fillField('#dateOfMarriageOrCP-month', applyForGrantOfProbateConfig.page5_dateOfMarriageOrCP_month);
        I.fillField('#dateOfMarriageOrCP-year', applyForGrantOfProbateConfig.page5_dateOfMarriageOrCP_year);

        I.fillField('#dateOfDivorcedCPJudicially-day', applyForGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_day);
        I.fillField('#dateOfDivorcedCPJudicially-month', applyForGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_month);
        I.fillField('#dateOfDivorcedCPJudicially-year', applyForGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_year);

        I.fillField('#courtOfDecree', applyForGrantOfProbateConfig.page5_courtOfDecree);
        I.click(`#willGiftUnderEighteen-${applyForGrantOfProbateConfig.page5_willGiftUnderEighteenYes}`);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page5_list1_update_option);
        I.click(commonConfig.continueButton);

        I.fillField('#willNumberOfCodicils', applyForGrantOfProbateConfig.page5_willNumberOfCodicils_update);

    }

    I.click(commonConfig.continueButton);
};
