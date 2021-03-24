'use strict';

const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#willExists-${createGrantOfProbateConfig.page6_willExistsYes}`);
        await I.click(`#willDatedBeforeApril-${createGrantOfProbateConfig.page6_willDatedBeforeAprilYes}`);
        await I.click(`#willAccessOriginal-${createGrantOfProbateConfig.page6_willAccessOriginalYes}`);
        await I.click(`#willHasCodicils-${createGrantOfProbateConfig.page6_willHasCodicilsYes}`);

        if (createGrantOfProbateConfig.page1_paperForm === 'No') {
            await I.fillField('#originalWillSignedDate-day', createGrantOfProbateConfig.page6_originalWillSignedDate_day);
            await I.fillField('#originalWillSignedDate-month', createGrantOfProbateConfig.page6_originalWillSignedDate_month);
            await I.fillField('#originalWillSignedDate-year', createGrantOfProbateConfig.page6_originalWillSignedDate_year);

            const addBtn = {css: '#codicilAddedDateList button'};
            await I.waitForVisible(addBtn);
            await I.scrollTo(addBtn);
            await I.waitForClickable(addBtn);
            await I.click(addBtn);
            if (!testConfig.TestAutoDelayEnabled) {
                await I.wait(0.25);
            }

            await I.waitForVisible({css: '#codicilAddedDateList_0_dateCodicilAdded-day'});
            await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-day'}, createGrantOfProbateConfig.page6_codicilDate_day);
            await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-month'}, createGrantOfProbateConfig.page6_codicilDate_month);
            await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-year'}, createGrantOfProbateConfig.page6_codicilDate_year);

        } else {
            await I.dontSeeElement('#originalWillSignedDate-day');
            await I.dontSeeElement('#originalWillSignedDate-month');
            await I.dontSeeElement('#originalWillSignedDate-year');
            await I.dontSeeElement('#codicilAddedDateList button');
        }

        await I.click({css: `#willsOutsideOfUK-${createGrantOfProbateConfig.page6_willsOutsideOfUKYes}`});
        await I.click({css: `#deceasedEnterMarriageOrCP-${createGrantOfProbateConfig.page6_deceasedEnterMarriageOrCPYes}`});

        await I.fillField({css: '#dateOfMarriageOrCP-day'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_day);
        await I.fillField({css: '#dateOfMarriageOrCP-month'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_month);
        await I.fillField({css: '#dateOfMarriageOrCP-year'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_year);

        await I.fillField({css: '#dateOfDivorcedCPJudicially-day'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_day);
        await I.fillField({css: '#dateOfDivorcedCPJudicially-month'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_month);
        await I.fillField({css: '#dateOfDivorcedCPJudicially-year'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_year);

        await I.fillField({css: '#courtOfDecree'}, createGrantOfProbateConfig.page6_courtOfDecree);
        await I.click({css: `#willGiftUnderEighteen-${createGrantOfProbateConfig.page6_willGiftUnderEighteenYes}`});
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page6_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption({css: '#selectionList'}, createGrantOfProbateConfig.page6_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        //        complete these when completing cw amend story
        //        await I.waitForElement({css: '#codicilAddedDateList_0_dateCodicilAdded-day'});
        // if (createGrantOfProbateConfig.page1_paperForm === 'No') {
        //        await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-day'}, createGrantOfProbateConfig.page6_codicilDate_day_update);
        // }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
