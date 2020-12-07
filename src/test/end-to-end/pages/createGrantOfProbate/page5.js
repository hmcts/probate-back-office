'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page5_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#willExists-${createGrantOfProbateConfig.page5_willExistsYes}`);
        await I.click(`#willDatedBeforeApril-${createGrantOfProbateConfig.page5_willDatedBeforeAprilYes}`);
        await I.click(`#willAccessOriginal-${createGrantOfProbateConfig.page5_willAccessOriginalYes}`);
        await I.click(`#willHasCodicils-${createGrantOfProbateConfig.page5_willHasCodicilsYes}`);
        await I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page5_willNumberOfCodicils);
        await I.click(`#willsOutsideOfUK-${createGrantOfProbateConfig.page5_willsOutsideOfUKYes}`);
        await I.click(`#deceasedEnterMarriageOrCP-${createGrantOfProbateConfig.page5_deceasedEnterMarriageOrCPYes}`);

        await I.fillField('#dateOfMarriageOrCP-day', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_day);
        await I.fillField('#dateOfMarriageOrCP-month', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_month);
        await I.fillField('#dateOfMarriageOrCP-year', createGrantOfProbateConfig.page5_dateOfMarriageOrCP_year);

        await I.fillField('#dateOfDivorcedCPJudicially-day', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_day);
        await I.fillField('#dateOfDivorcedCPJudicially-month', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_month);
        await I.fillField('#dateOfDivorcedCPJudicially-year', createGrantOfProbateConfig.page5_dateOfDivorcedCPJudicially_year);

        await I.fillField('#courtOfDecree', createGrantOfProbateConfig.page5_courtOfDecree);
        await I.click(`#willGiftUnderEighteen-${createGrantOfProbateConfig.page5_willGiftUnderEighteenYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page5_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page5_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page5_willNumberOfCodicils_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
