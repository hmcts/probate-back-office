'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#willExists-${createGrantOfProbateConfig.page6_willExistsYes}`);
        await I.click(`#willDatedBeforeApril-${createGrantOfProbateConfig.page6_willDatedBeforeAprilYes}`);
        await I.click(`#willAccessOriginal-${createGrantOfProbateConfig.page6_willAccessOriginalYes}`);
        await I.click(`#willHasCodicils-${createGrantOfProbateConfig.page6_willHasCodicilsYes}`);
        await I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page6_willNumberOfCodicils);
        await I.click(`#willsOutsideOfUK-${createGrantOfProbateConfig.page6_willsOutsideOfUKYes}`);
        await I.click(`#deceasedEnterMarriageOrCP-${createGrantOfProbateConfig.page6_deceasedEnterMarriageOrCPYes}`);

        await I.fillField('#dateOfMarriageOrCP-day', createGrantOfProbateConfig.page6_dateOfMarriageOrCP_day);
        await I.fillField('#dateOfMarriageOrCP-month', createGrantOfProbateConfig.page6_dateOfMarriageOrCP_month);
        await I.fillField('#dateOfMarriageOrCP-year', createGrantOfProbateConfig.page6_dateOfMarriageOrCP_year);

        await I.fillField('#dateOfDivorcedCPJudicially-day', createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_day);
        await I.fillField('#dateOfDivorcedCPJudicially-month', createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_month);
        await I.fillField('#dateOfDivorcedCPJudicially-year', createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_year);

        await I.fillField('#courtOfDecree', createGrantOfProbateConfig.page6_courtOfDecree);
        await I.click(`#willGiftUnderEighteen-${createGrantOfProbateConfig.page6_willGiftUnderEighteenYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page6_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page6_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.fillField('#willNumberOfCodicils', createGrantOfProbateConfig.page6_willNumberOfCodicils_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
