const { expect } = require('@playwright/test');
const { testConfig } = require ('../../Configs/config');
const { BasePage } = require ('../utility/basePage');
const newCaseConfig = require('./newCaseConfig');
const createCaseConfig = require("../createCase/createCaseConfig");
const createCaveatConfig = require('../createCaveat/createCaveatConfig');
const commonConfig = require('../common/commonConfig');
const checkYourAnswersConfig = require('../checkYourAnswers/checkYourAnswersConfig');
const eventSummaryConfig = require('../eventSummary/eventSummaryConfig');

exports.CreateCasePage = class CreateCasePage extends BasePage {
     constructor(page) {
        super(page);
        this.page = page;
        this.createCasePageLocator = page.getByRole('link', { name: newCaseConfig.waitForText});
        this.createCaseLocator = page.getByRole('link', { name: newCaseConfig.xuiCreateCaseLocator});
        this.jurisdictionLocator = page.getByLabel(newCaseConfig.jurisdictionLocatorName);
        this.caseTypeLocator = page.getByLabel(newCaseConfig.caseTypeLocatorName);
        this.eventLocator = page.getByLabel(newCaseConfig.eventLocatorName);
        this.startButtonLocator = page.getByRole('button', { name: newCaseConfig.startButton });
        this.createCaveatPageLocator = page.getByText(createCaveatConfig.page1_waitForText);
        this.applicationTypeLocator = page.getByLabel(newCaseConfig.applicationTypeLocatorName);
        this.registryLocator = page.getByLabel(newCaseConfig.registryLocatorName);
        this.amendCaveatPageLocator = page.getByText(createCaveatConfig.page1_amend_waitForText);
        this.createCaveatPage2Locator = page.getByText(createCaveatConfig.page2_waitForText);
        this.postcodeLinkLocator = page.getByText(createCaveatConfig.UKpostcodeLink);
        this.amendCaveatPage2Locator = page.getByText(createCaveatConfig.page2_amend_waitForText);
        this.createCaveatPage3Locator = page.getByText(createCaveatConfig.page3_waitForText);
        this.amendCaveatPage3Locator = page.getByText(createCaveatConfig.page3_amend_waitForText);
        this.amendCaveatPage4Locator = page.getByText(createCaveatConfig.page4_amend_waitForText);
        this.checkYourAnswersHeadingLocator = page.getByText(checkYourAnswersConfig.waitForText);
    }

    async selectNewCase() {
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCasePageLocator).toBeVisible();
        await this.rejectCookies();
        await expect(this.createCaseLocator).toBeEnabled();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.createCaseLocator.click();
    }

    async selectCaseTypeOptions(caseType, event){
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCaseLocator).toBeVisible();
        await expect(this.jurisdictionLocator).toBeEnabled();
        await this.jurisdictionLocator.selectOption({value: newCaseConfig.jurisdictionValue});
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.caseTypeLocator).toBeEnabled();
        await this.caseTypeLocator.selectOption({value: caseType});
        await expect(this.page.getByRole('option', {name: caseType}).first()).toBeHidden();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.eventLocator).toBeEnabled();
        await this.eventLocator.selectOption({label: event});
        await expect(this.page.getByRole('option', {name: event})).toBeHidden();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.startButtonLocator).toBeEnabled();
        await this.startButtonLocator.click();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
    }

    async enterCaveatPage1(crud){
        if (crud === 'create') {
            await expect(this.createCaveatPageLocator).toBeVisible();
            await expect(this.applicationTypeLocator).toBeEnabled();
            await this.applicationTypeLocator.selectOption({value: newCaseConfig.page1_list1_application_type});
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({value: newCaseConfig.page1_list2_registry_location});
            // await I.waitForText(createCaveatConfig.page1_waitForText, testConfig.WaitForTextTimeout);

            // await I.waitForEnabled('#applicationType');
            // await I.selectOption('#applicationType', createCaveatConfig.page1_list1_application_type);
            // await I.waitForEnabled('#registryLocation');
            // await I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location);
        }

        if (crud === 'update') {
            await expect(this.amendCaveatPageLocator).toBeVisible();
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({value: newCaseConfig.page1_list2_registry_location_update});
            // await I.waitForText(createCaveatConfig.page1_amend_waitForText, testConfig.WaitForTextTimeout);

            // await I.waitForEnabled('#registryLocation');
            // await I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location_update);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterCaveatPage2(crud, unique_deceased_user){
        if (crud === 'create') {
            await expect(this.createCaveatPage2Locator).toBeVisible();
            await this.page.locator('#deceasedForenames').fill(createCaveatConfig.page2_forenames+unique_deceased_user);
            await this.page.locator('#deceasedSurname').fill(createCaveatConfig.page2_surname+unique_deceased_user);
            await this.page.locator('#deceasedDateOfDeath-day').fill(createCaveatConfig.page2_dateOfDeath_day);
            await this.page.locator('#deceasedDateOfDeath-month').fill(createCaveatConfig.page2_dateOfDeath_month);
            await this.page.locator('#deceasedDateOfDeath-year').fill(createCaveatConfig.page2_dateOfDeath_year);
            await this.page.locator(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`).focus();
            await this.page.locator(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`).check();
            // await I.waitForText(createCaveatConfig.page2_waitForText, testConfig.WaitForTextTimeout);

            // await I.fillField('#deceasedForenames', createCaveatConfig.page2_forenames+unique_deceased_user);
            // await I.fillField('#deceasedSurname', createCaveatConfig.page2_surname+unique_deceased_user);
            //
            // await I.fillField('#deceasedDateOfDeath-day', createCaveatConfig.page2_dateOfDeath_day);
            // await I.fillField('#deceasedDateOfDeath-month', createCaveatConfig.page2_dateOfDeath_month);
            // await I.fillField('#deceasedDateOfDeath-year', createCaveatConfig.page2_dateOfDeath_year);

            // await I.click(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`);

            let idx = 0;
            /* eslint-disable no-await-in-loop */
            const keys = Object.keys(createCaveatConfig);
            for (let i=0; i < keys.length; i++) {
                const propName = keys[i];
                if(idx == 0) {
                    this.addNewButtonLocator = this.page.getByRole('button', {name: createCaveatConfig.page2_addAliasButton}).first();
                } else {
                    this.addNewButtonLocator = this.page.getByRole('button', {name: createCaveatConfig.page2_addAliasButton}).nth(1);
                }

                if (propName.includes('page2_alias_')) {
                    await this.addNewButtonLocator.click();
                    if (!testConfig.TestAutoDelayEnabled) {
                        await this.page.waitForTimeout(testConfig.ManualDelayShort); // implicit wait needed here
                    }
                    await expect(this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`)).toBeEnabled();
                    await this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`).fill(createCaveatConfig[propName]);
                    idx += 1;
                }
            }

            await this.postcodeLinkLocator.focus();
            await this.postcodeLinkLocator.click();
            if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayShort); // implicit wait needed here
            }
            await this.page.locator('#deceasedAddress__detailAddressLine1').fill(createCaveatConfig.address_line1);
            await this.page.locator('#deceasedAddress__detailAddressLine2').fill(createCaveatConfig.address_line2);
            await this.page.locator('#deceasedAddress__detailAddressLine3').fill(createCaveatConfig.address_line3);
            await this.page.locator('#deceasedAddress__detailPostTown').fill(createCaveatConfig.address_town);
            await this.page.locator('#deceasedAddress__detailCounty').fill(createCaveatConfig.address_county);
            await this.page.locator('#deceasedAddress__detailPostCode').fill(createCaveatConfig.address_postcode);
            await this.page.locator('#deceasedAddress__detailCountry').fill(createCaveatConfig.address_country);
        }

        if (crud === 'update') {
            await expect(this.amendCaveatPage2Locator).toBeVisible();
            await this.page.locator('#deceasedForenames').fill(createCaveatConfig.page2_forenames_update+unique_deceased_user);
            await this.page.locator('#deceasedSurname').fill(createCaveatConfig.page2_surname_update+unique_deceased_user);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterCaveatPage3(crud){
        if (crud === 'create') {
            await expect(this.createCaveatPage3Locator).toBeVisible();
            // await I.waitForText(createCaveatConfig.page3_waitForText, testConfig.WaitForTextTimeout);

            await this.page.locator('#caveatorForenames').fill(createCaveatConfig.page3_caveator_forenames);
            await this.page.locator('#caveatorSurname').fill(createCaveatConfig.page3_caveator_surname);
            await this.page.locator('#caveatorEmailAddress').fill(createCaveatConfig.page3_caveator_email);
            await this.page.locator('#solsSolicitorAppReference').fill(createCaveatConfig.page3_solAppReference);

            await this.postcodeLinkLocator.focus();
            await this.postcodeLinkLocator.click();
            await this.page.locator('#caveatorAddress__detailAddressLine1').fill(createCaveatConfig.address_line1);
            await this.page.locator('#caveatorAddress__detailAddressLine2').fill(createCaveatConfig.address_line2);
            await this.page.locator('#caveatorAddress__detailAddressLine3').fill(createCaveatConfig.address_line3);
            await this.page.locator('#caveatorAddress__detailPostTown').fill(createCaveatConfig.address_town);
            await this.page.locator('#caveatorAddress__detailCounty').fill(createCaveatConfig.address_county);
            await this.page.locator('#caveatorAddress__detailPostCode').fill(createCaveatConfig.address_postcode);
            await this.page.locator('#caveatorAddress__detailCountry').fill(createCaveatConfig.address_country);
            await this.page.locator(`#languagePreferenceWelsh_${createCaveatConfig.page3_langPrefNo}`).click();
        }

        if (crud === 'update') {
            await expect(this.amendCaveatPage3Locator).toBeVisible();
            // await I.waitForText(createCaveatConfig.page3_amend_waitForText, testConfig.WaitForTextTimeout);

            await this.page.locator('#caveatorForenames').fill(createCaveatConfig.page3_caveator_forenames_update);
            await this.page.locator('#caveatorSurname').fill(createCaveatConfig.page3_caveator_surname_update);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterCaveatPage4(crud){
        if (crud === 'update') {
            await expect(this.amendCaveatPage4Locator).toBeVisible();
            // await I.waitForText(createCaveatConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);
            await I.waitForEnabled({css: '#expiryDate-day'}, testConfig.WaitForTextTimeout);

            await this.page.locator('#expiryDate-day').fill(createCaveatConfig.page4_caveatExpiryDate_day_update);
            await this.page.locator('#expiryDate-month').fill(createCaveatConfig.page4_caveatExpiryDate_month_update);
            await this.page.locator('#expiryDate-year').fill(createCaveatConfig.page4_caveatExpiryDate_year_update);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async checkMyAnswers(nextStepName){
        let eventSummaryPrefix = nextStepName;
        await expect(this.checkYourAnswersHeadingLocator).toBeVisible();
        // await I.waitForText(checkYourAnswersConfig.waitForText, testConfig.WaitForTextTimeout);

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await this.page.locator('#field-trigger-summary').fill(eventSummaryPrefix + eventSummaryConfig.summary);
        await this.page.locator('#field-trigger-description').fill(eventSummaryPrefix + eventSummaryConfig.comment);

        await this.waitForSubmitNavigationToComplete();
    }
};