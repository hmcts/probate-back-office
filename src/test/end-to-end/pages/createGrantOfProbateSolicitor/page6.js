'use strict';

const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page6_waitForText, testConfig.WaitForTextTimeout);
        await I.click({css: '#willExists_Yes'});
        await I.waitForElement({css: '#willHasCodicils_Yes'});
        await I.click({css: `#willDatedBeforeApril_${createGrantOfProbateConfig.page6_willDatedBeforeAprilYes}`});
        await I.click({css: '#willAccessOriginal_No'});

        if (createGrantOfProbateConfig.page1_paperForm === 'No') {
            await I.waitForVisible({css: '#noOriginalWillAccessReason'});
            await I.click({css: `#willAccessOriginal_${createGrantOfProbateConfig.page6_willAccessOriginalYes}`});
            await I.waitForInvisible({css: '#noOriginalWillAccessReason'});
            await I.click({css: '#willHasCodicils_Yes'});

            await I.fillField({css: '#originalWillSignedDate-day'}, createGrantOfProbateConfig.page6_originalWillSignedDate_day);
            await I.fillField({css: '#originalWillSignedDate-month'}, createGrantOfProbateConfig.page6_originalWillSignedDate_month);
            await I.fillField({css: '#originalWillSignedDate-year'}, createGrantOfProbateConfig.page6_originalWillSignedDate_year);

            const addBtn = {css: '#codicilAddedDateList button'};
            await I.waitForVisible(addBtn);
            await I.scrollTo(addBtn);
            await I.waitForClickable(addBtn);
            await I.click(addBtn);
            if (!testConfig.TestAutoDelayEnabled) {
                await I.wait(testConfig.ManualDelayShort);
            }

            await I.waitForVisible({css: '#dateCodicilAdded-day'});
            await I.fillField({css: '#dateCodicilAdded-day'}, createGrantOfProbateConfig.page6_codicilDate_day);
            await I.fillField({css: '#dateCodicilAdded-month'}, createGrantOfProbateConfig.page6_codicilDate_month);
            await I.fillField({css: '#dateCodicilAdded-year'}, createGrantOfProbateConfig.page6_codicilDate_year);

        } else {
            await I.waitForClickable({css: '#willHasCodicils_No'});
            await I.click({css: '#willHasCodicils_No'});
            await I.dontSeeElement({css: '#noOriginalWillAccessReason'});
            await I.dontSeeElement({css: '#originalWillSignedDate-day'});
            await I.dontSeeElement({css: '#originalWillSignedDate-month'});
            await I.dontSeeElement({css: '#originalWillSignedDate-year'});
            await I.dontSeeElement({css: '#codicilAddedDateList button'});
            await I.click({css: `#willAccessOriginal_${createGrantOfProbateConfig.page6_willAccessOriginalYes}`});
        }

        await I.click({css: `#willsOutsideOfUK_${createGrantOfProbateConfig.page6_willsOutsideOfUKYes}`});
        await I.click({css: `#deceasedEnterMarriageOrCP_${createGrantOfProbateConfig.page6_deceasedEnterMarriageOrCPYes}`});

        await I.fillField({css: '#dateOfMarriageOrCP-day'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_day);
        await I.fillField({css: '#dateOfMarriageOrCP-month'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_month);
        await I.fillField({css: '#dateOfMarriageOrCP-year'}, createGrantOfProbateConfig.page6_dateOfMarriageOrCP_year);

        await I.fillField({css: '#dateOfDivorcedCPJudicially-day'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_day);
        await I.fillField({css: '#dateOfDivorcedCPJudicially-month'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_month);
        await I.fillField({css: '#dateOfDivorcedCPJudicially-year'}, createGrantOfProbateConfig.page6_dateOfDivorcedCPJudicially_year);

        await I.fillField({css: '#courtOfDecree'}, createGrantOfProbateConfig.page6_courtOfDecree);
        await I.click({css: `#willGiftUnderEighteen_${createGrantOfProbateConfig.page6_willGiftUnderEighteenYes}`});
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page6_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.selectOption({css: '#selectionList'}, createGrantOfProbateConfig.page6_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        if (createGrantOfProbateConfig.page1_paperForm === 'No') {
            await I.waitForElement({css: '#dateCodicilAdded-day'});
            await I.fillField({css: '#dateCodicilAdded-day'}, createGrantOfProbateConfig.page6_codicilDate_day_update);
        }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
