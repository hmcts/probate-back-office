const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const {BasePage} = require ('../utility/basePage');
const newCaseConfig = require('./newCaseConfig');
const createCaveatConfig = require('../createCaveat/createCaveatConfig');
const commonConfig = require('../common/commonConfig');
const checkYourAnswersConfig = require('../checkYourAnswers/checkYourAnswersConfig');
const eventSummaryConfig = require('../eventSummary/eventSummaryConfig');
const createGrantOfProbateConfig = require('../createGrantOfProbateManual/createGrantOfProbateManualConfig');
const createGrantofProbateAmendConfig = require('../createGrantofProbate/createGrantOfProbateConfig');
const caseProgressConfig = require('../caseProgressStandard/caseProgressConfig');

exports.CreateCasePage = class CreateCasePage extends BasePage {
    constructor(page) {
        super(page);
        this.page = page;
        this.createCasePageLocator = page.getByRole('link', {name: newCaseConfig.waitForText});
        this.createCaseLocator = page.getByRole('link', {name: newCaseConfig.xuiCreateCaseLocator});
        this.jurisdictionLocator = page.getByLabel(newCaseConfig.jurisdictionLocatorName);
        this.caseTypeLocator = this.page.locator('#cc-case-type');
        this.eventLocator = page.getByLabel(newCaseConfig.eventLocatorName);
        this.startButtonLocator = page.getByRole('button', {name: newCaseConfig.startButton});
        this.createCaveatPageLocator = page.getByText(createCaveatConfig.page1_waitForText);
        this.applicationTypeLocator = page.getByLabel(newCaseConfig.applicationTypeLocatorName);
        this.registryLocator = this.page.locator('#registryLocation');
        this.applicationTypeLocator = this.page.locator('#applicationType');
        this.caseTypeIdLocator = this.page.locator('#caseType');
        this.createCaseCwTextLocator = page.getByText(createGrantOfProbateConfig.page1_waitForText);
        this.amendCaveatPageLocator = page.getByText(createCaveatConfig.page1_amend_waitForText);
        this.createCaveatPage2Locator = page.getByText(createCaveatConfig.page2_waitForText);
        this.postcodeLinkLocator = page.getByText(createCaveatConfig.UKpostcodeLink);
        this.amendCaveatPage2Locator = page.getByText(createCaveatConfig.page2_amend_waitForText);
        this.createCaveatPage3Locator = page.getByText(createCaveatConfig.page3_waitForText);
        this.amendCaveatPage3Locator = page.getByText(createCaveatConfig.page3_amend_waitForText);
        this.amendCaveatPage4Locator = page.getByText(createCaveatConfig.page4_amend_waitForText);
        this.primaryApplicantApplyingLocator = this.page.locator(`#primaryApplicantIsApplying_${createGrantOfProbateConfig.page1_applyingYes}`);
        this.checkYourAnswersHeadingLocator = page.getByText(checkYourAnswersConfig.waitForText);
        this.dateOfDeathTypeLocator = this.page.locator('#dateOfDeathType');
        this.deceasedDomicileEngLocator = this.page.locator('#deceasedDomicileInEngWales_Yes');
        this.deceasedAliasLocator = page.getByRole('group', {name: `${createGrantOfProbateConfig.page1_deceasedAnyOtherName}`}).getByLabel(`${createGrantOfProbateConfig.page1_deceasedAnyOtherNamesNo}`);
        this.ihtPageWaitForTextLocator = page.getByRole('heading', {name: `${createGrantOfProbateConfig.EE_waitForText}`});
        this.iht205Locator = this.page.getByText(caseProgressConfig.IHT205Label);
        this.iht400Locator = this.page.getByText(caseProgressConfig.IHT400Label);
        this.pcLocator = this.page.locator(`xpath=${createGrantOfProbateConfig.UKpostcodeLink}`);
        this.pcLocator2 = this.page.locator(`xpath=${createGrantOfProbateConfig.UKpostcodeLink2}`);
        this.page4waitForTextLocator = this.page.locator(createGrantOfProbateConfig.page4_waitForText);
        this.deceasedTitleLocator = this.page.locator('#boDeceasedTitle');
        this.deceasedAddressLocator = this.page.locator('#deceasedAddress__detailAddressLine1');
        this.deceasedAliasNameLocator = this.page.locator('#solsDeceasedAliasNamesList_0_SolsAliasname');
        this.foreignAssetLocator = this.page.locator('#foreignAssetEstateValue');
        this.amendHeadingLocator = page.getByRole('heading', {name: `${createGrantofProbateAmendConfig.page4_amend_waitForText}`});
        this.amendDetailSelectionLocator = this.page.locator('#selectionList');
        this.deceasedForenameLocator = this.page.locator('#deceasedForenames');
        this.deceasedDodDayLocator = this.page.locator('#deceasedDateOfDeath-day');
    }

    async selectNewCase() {
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCasePageLocator).toBeVisible();
        await this.rejectCookies();
        await expect(this.createCaseLocator).toBeEnabled();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.createCaseLocator.click();
    }

    async selectCaseTypeOptions(caseType, event) {

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

    async enterCaveatPage1(crud) {
        if (crud === 'create') {
            await expect(this.createCaveatPageLocator).toBeVisible();
            await expect(this.applicationTypeLocator).toBeEnabled();
            await this.applicationTypeLocator.selectOption({label: newCaseConfig.page1_list1_application_type});
            await expect(this.registryLocator).toBeVisible();
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({label: newCaseConfig.page1_list2_registry_location});

        }

        if (crud === 'update') {
            await expect(this.amendCaveatPageLocator).toBeVisible();
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({label: newCaseConfig.page1_list2_registry_location_update});
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterCaveatPage2(crud, unique_deceased_user) {
        if (crud === 'create') {
            await expect(this.createCaveatPage2Locator).toBeVisible();
            await this.page.locator('#deceasedForenames').fill(createCaveatConfig.page2_forenames+unique_deceased_user);
            await this.page.locator('#deceasedSurname').fill(createCaveatConfig.page2_surname+unique_deceased_user);
            await this.page.locator('#deceasedDateOfDeath-day').fill(createCaveatConfig.page2_dateOfDeath_day);
            await this.page.locator('#deceasedDateOfDeath-month').fill(createCaveatConfig.page2_dateOfDeath_month);
            await this.page.locator('#deceasedDateOfDeath-year').fill(createCaveatConfig.page2_dateOfDeath_year);
            await this.page.locator(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`).focus();
            await this.page.locator(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`).check();

            let idx = 0;
            /* eslint-disable no-await-in-loop */
            const keys = Object.keys(createCaveatConfig);
            for (let i=0; i < keys.length; i++) {
                const propName = keys[i];
                if (idx === 0) {
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

    async enterCaveatPage3(crud) {
        if (crud === 'create') {
            await expect(this.createCaveatPage3Locator).toBeVisible();

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
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }

        if (crud === 'update') {
            await expect(this.amendCaveatPage3Locator).toBeVisible();
            // await I.waitForText(createCaveatConfig.page3_amend_waitForText, testConfig.WaitForTextTimeout);

            await this.page.locator('#caveatorForenames').fill(createCaveatConfig.page3_caveator_forenames_update);
            await this.page.locator('#caveatorSurname').fill(createCaveatConfig.page3_caveator_surname_update);
            await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
        }

    }

    async enterCaveatPage4(crud) {
        if (crud === 'update') {
            await expect(this.amendCaveatPage4Locator).toBeVisible();
            await expect(this.page.locator('#expiryDate-day')).toBeEnabled();

            await this.page.locator('#expiryDate-day').fill(createCaveatConfig.page4_caveatExpiryDate_day_update);
            await this.page.locator('#expiryDate-month').fill(createCaveatConfig.page4_caveatExpiryDate_month_update);
            await this.page.locator('#expiryDate-year').fill(createCaveatConfig.page4_caveatExpiryDate_year_update);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbateManualPage1(crud, unique_deceased_user, deceasedDODYear) {
        if (crud === 'create') {
            await expect(this.createCaseCwTextLocator)
                .toBeVisible();
            await expect(this.page.locator('#registryLocation')).toBeEnabled;
            await this.registryLocator.selectOption({label: createGrantOfProbateConfig.page1_list1_registry_location});
            await this.applicationTypeLocator.selectOption({label: createGrantOfProbateConfig.page1_list2_application_type});

            await this.page.locator('#applicationSubmittedDate-day')
                .fill(createGrantOfProbateConfig.page1_applicationSubmittedDate_day);
            await this.page.locator('#applicationSubmittedDate-month')
                .fill(createGrantOfProbateConfig.page1_applicationSubmittedDate_month);
            await this.page.locator('#applicationSubmittedDate-year')
                .fill(createGrantOfProbateConfig.page1_applicationSubmittedDate_year);

            await this.caseTypeIdLocator.selectOption({label: createGrantOfProbateConfig.page1_list3_case_type});

            await this.page.locator('#extraCopiesOfGrant')
                .fill(createGrantOfProbateConfig.page1_extraCopiesOfGrant);
            await this.page.locator('#outsideUKGrantCopies')
                .fill(createGrantOfProbateConfig.page1_outsideUKGrantCopies);

            await expect(this.createCaseCwTextLocator).toBeVisible();
            await this.page.locator('#primaryApplicantForenames')
                .fill(createGrantOfProbateConfig.page1_firstnames);
            await this.page.locator('#primaryApplicantSurname')
                .fill(createGrantOfProbateConfig.page1_lastnames);
            await this.primaryApplicantApplyingLocator.click();
            await this.page.locator('#primaryApplicantEmailAddress')
                .fill(createGrantOfProbateConfig.page1_email);

            if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
                // await I.wait(testConfig.ManualDelayShort);
            }

            await this.pcLocator.click();
            await expect(this.page.locator('#primaryApplicantAddress__detailAddressLine1')).toBeVisible();
            await this.page.locator('#primaryApplicantAddress__detailAddressLine1')
                .fill(createGrantOfProbateConfig.address_line1);
            await this.page.locator('#primaryApplicantAddress__detailAddressLine2')
                .fill(createGrantOfProbateConfig.address_line2);
            await this.page.locator('#primaryApplicantAddress__detailAddressLine3')
                .fill(createGrantOfProbateConfig.address_line3);
            await this.page.locator('#primaryApplicantAddress__detailPostTown')
                .fill(createGrantOfProbateConfig.address_town);
            await this.page.locator('#primaryApplicantAddress__detailCounty')
                .fill(createGrantOfProbateConfig.address_county);
            await this.page.locator('#primaryApplicantAddress__detailPostCode')
                .fill(createGrantOfProbateConfig.address_postcode);
            await this.page.locator('#primaryApplicantAddress__detailCountry')
                .fill(createGrantOfProbateConfig.address_country);

            await this.page.locator(`#otherExecutorExists_${createGrantOfProbateConfig.page1_otherExecutorExistsNo}`).click();
            await this.page.locator('#boDeceasedTitle')
                .fill(createGrantOfProbateConfig.page1_bo_deceasedTitle);
            await this.page.locator('#deceasedForenames')
                .fill(createGrantOfProbateConfig.page1_deceasedForenames + '_' + unique_deceased_user);
            await this.page.locator('#deceasedSurname')
                .fill(createGrantOfProbateConfig.page1_deceasedSurname + '_' + unique_deceased_user);
            await this.page.locator('#boDeceasedHonours')
                .fill(createGrantOfProbateConfig.page1_bo_deceasedHonours);

            await expect(this.pcLocator2).toBeVisible();
            await this.pcLocator2.click();

            await expect(this.page.locator('#deceasedAddress__detailAddressLine1')).toBeVisible();
            await this.page.locator('#deceasedAddress__detailAddressLine1')
                .fill(createGrantOfProbateConfig.address_line1);
            await this.page.locator('#deceasedAddress__detailAddressLine2')
                .fill(createGrantOfProbateConfig.address_line2);
            await this.page.locator('#deceasedAddress__detailAddressLine3')
                .fill(createGrantOfProbateConfig.address_line3);
            await this.page.locator('#deceasedAddress__detailPostTown')
                .fill(createGrantOfProbateConfig.address_town);
            await this.page.locator('#deceasedAddress__detailCounty')
                .fill(createGrantOfProbateConfig.address_county);
            await this.page.locator('#deceasedAddress__detailPostCode')
                .fill(createGrantOfProbateConfig.address_postcode);
            await this.page.locator('#deceasedAddress__detailCountry')
                .fill(createGrantOfProbateConfig.address_country);

            await expect(this.dateOfDeathTypeLocator).toBeEnabled();
            await this.dateOfDeathTypeLocator.selectOption({label: `${createGrantOfProbateConfig.page1_dateOfDeathType}`});
            await this.page.locator('#deceasedDateOfBirth-day')
                .fill(createGrantOfProbateConfig.page1_deceasedDob_day);
            await this.page.locator('#deceasedDateOfBirth-month')
                .fill(createGrantOfProbateConfig.page1_deceasedDob_month);
            await this.page.locator('#deceasedDateOfBirth-year')
                .fill(createGrantOfProbateConfig.page1_deceasedDob_year);
            await this.page.locator('#deceasedDateOfDeath-day')
                .fill(createGrantOfProbateConfig.page1_deceasedDod_day);
            await this.page.locator('#deceasedDateOfDeath-month')
                .fill(createGrantOfProbateConfig.page1_deceasedDod_month);
            await this.page.locator('#deceasedDateOfDeath-year')
                .fill(deceasedDODYear);

            await expect(this.deceasedAliasLocator).toBeEnabled();

            // The purpose of clicking the same element twice is because of the EXUI issue where
            // a radio button or checkbox with a hint or subtask defined is not recognised on
            // the first click event
            await this.deceasedAliasLocator.click();
            await this.deceasedAliasLocator.click();
            await expect(this.deceasedAliasLocator).toBeChecked();
            await expect(this.deceasedDomicileEngLocator).toBeEnabled();
            await this.deceasedDomicileEngLocator.click();
            await this.deceasedDomicileEngLocator.click();
            await this.page.locator(`#languagePreferenceWelsh_${createGrantOfProbateConfig.page1_optionNo}`).click();
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbateManualPage2(crud) {
        if (crud === 'create') {
            await expect(this.ihtPageWaitForTextLocator).toBeVisible();
            await this.page.locator(`#ihtFormEstateValuesCompleted_${createGrantOfProbateConfig.EE_ihtFormEstateValueCompletedNo}`).click();
            await this.page.locator(`#ihtFormEstateValuesCompleted_${createGrantOfProbateConfig.EE_ihtFormEstateValueCompletedNo}`).click();

            await this.page.locator('#ihtEstateGrossValue')
                .fill(createGrantOfProbateConfig.EE_ihtEstateGrossValue);
            await this.page.locator('#ihtEstateNetValue')
                .fill(createGrantOfProbateConfig.EE_ihtEstateNetValue);
            await this.page.locator('#ihtEstateNetQualifyingValue')
                .fill(createGrantOfProbateConfig.EE_ihtEstateNetValue);

            await this.page.locator(`#deceasedHadLateSpouseOrCivilPartner_${createGrantOfProbateConfig.EE_deceasedHadLateSpouseOrCivilPartnerYes}`).click();
            await this.page.locator(`#ihtUnusedAllowanceClaimed_${createGrantOfProbateConfig.EE_ihtUnusedAllowanceClaimed_No}`).click();
        } else {
            await expect(this.page.getByText(caseProgressConfig.IHT205Label)).toBeVisible();
            await expect(this.page.locator('#ihtFormId').getByText(caseProgressConfig.IHT400Label)).toBeVisible();
            await this.page.locator(`#ihtFormId-${caseProgressConfig.IHT400Option}`).click();
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbateManualPage3(crud) {
        if (crud === 'create') {
            await expect(this.createCaseCwTextLocator).toBeVisible();

            await this.page.locator('#ihtGrossValue')
                .fill(createGrantOfProbateConfig.EE_ihtEstateGrossValue);
            await this.page.locator('#ihtNetValue')
                .fill(createGrantOfProbateConfig.EE_ihtEstateNetValue);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbatePage4(crud, unique_deceased_user) {
        if (crud === 'create') {
            await expect(this.page4waitForTextLocator).toBeVisible();
            await expect(this.deceasedTitleLocator).toBeEnabled();
            await this.deceasedTitleLocator
                .fill(createGrantofProbateAmendConfig.page4_bo_deceasedTitle);
            await this.page.locator('#deceasedForenames')
                .fill(createGrantofProbateAmendConfig.page4_deceasedForenames + '_' + unique_deceased_user);
            await this.page.locator('#deceasedSurname')
                .fill(createGrantofProbateAmendConfig.page4_deceasedSurname + '_' + unique_deceased_user);
            await this.page.locator('#boDeceasedHonours')
                .fill(createGrantofProbateAmendConfig.page4_bo_deceasedHonours);

            await expect(this.pclocator).toBeVisible();
            await this.pcLocator.click();
            await expect(this.deceasedAddressLocator).toBeVisible();
            await this.deceasedAddressLocator
                .fill(createGrantofProbateAmendConfig.address_line1);
            await this.page.locator('#deceasedAddress__detailAddressLine2')
                .fill(createGrantofProbateAmendConfig.address_line2);
            await this.page.locator('#deceasedAddress__detailAddressLine3')
                .fill(createGrantofProbateAmendConfig.address_line3);
            await this.page.locator('#deceasedAddress__detailPostTown')
                .fill(createGrantofProbateAmendConfig.address_town);
            await this.page.locator('deceasedAddress__detailCounty')
                .fill(createGrantofProbateAmendConfig.address_county);
            await this.page.locator('#deceasedAddress__detailPostCode')
                .fill(createGrantofProbateAmendConfig.address_postcode);
            await this.page.locator('#deceasedAddress__detailCountry')
                .fill(createGrantofProbateAmendConfig.address_country);

            await this.dateOfDeathTypeLocator.selectOption({label: `${createGrantofProbateAmendConfig.page4_dateOfDeathType}`});
            await this.page.locator('#deceasedDateOfBirth-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_day);
            await this.page.locator('#deceasedDateOfBirth-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_month);
            await this.page.locator('#deceasedDateOfBirth-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_year);
            await this.page.locator('#deceasedDateOfDeath-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_day);
            await this.page.locator('#deceasedDateOfDeath-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_month);
            await this.page.locator('#deceasedDateOfDeath-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_year);

            await this.page.locator(`#deceasedAnyOtherNames_${createGrantofProbateAmendConfig.page4_deceasedAnyOtherNamesYes}`).click();
            await this.page.locator('#solsDeceasedAliasNamesList > div > button').click();
            await expect(this.deceasedAliasNameLocator).toBeVisible();
            await this.deceasedAliasNameLocator
                .fill(createGrantofProbateAmendConfig.page4_deceasedAlias);
            await this.page.locator(`#deceasedMaritalStatus-${createGrantofProbateAmendConfig.page4_deceasedMaritalStatus}`).click();
            await this.page.locator(`#foreignAsset_${createGrantofProbateAmendConfig.page4_foreignAssetYes}`).click();
            await expect(this.foreignAssetLocator).toBeVisible();
            await this.foreignAssetLocator
                .fill(createGrantofProbateAmendConfig.page4_foreignAssetEstateValue);

            // await I.waitForText(createGrantOfProbateConfig.page4_waitForText, testConfig.WaitForTextTimeout);
            // await I.waitForElement({css: '#boDeceasedTitle'});
            // await I.fillField({css: '#boDeceasedTitle'}, createGrantOfProbateConfig.page4_bo_deceasedTitle);

            // await I.fillField({css: '#deceasedForenames'}, createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user);
            // await I.fillField({css: '#deceasedSurname'}, createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user);
            // await I.fillField('#boDeceasedHonours', createGrantOfProbateConfig.page4_bo_deceasedHonours);

            // const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
            // await I.waitForVisible(pcLocator);
            // await I.click(pcLocator);

            // await I.waitForVisible({css: '#deceasedAddress__detailAddressLine1'});
            // await I.fillField('#deceasedAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
            // await I.fillField('#deceasedAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
            // await I.fillField('#deceasedAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
            // await I.fillField('#deceasedAddress__detailPostTown', createGrantOfProbateConfig.address_town);
            // await I.fillField('#deceasedAddress__detailCounty', createGrantOfProbateConfig.address_county);
            // await I.fillField('#deceasedAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
            // await I.fillField('#deceasedAddress__detailCountry', createGrantOfProbateConfig.address_country);

            // await I.selectOption({css: '#dateOfDeathType'}, createGrantOfProbateConfig.page4_dateOfDeathType);
            // await I.fillField({css: '#deceasedDateOfBirth-day'}, createGrantOfProbateConfig.page4_deceasedDob_day);
            // await I.fillField({css: '#deceasedDateOfBirth-month'}, createGrantOfProbateConfig.page4_deceasedDob_month);
            // await I.fillField({css: '#deceasedDateOfBirth-year'}, createGrantOfProbateConfig.page4_deceasedDob_year);
            // await I.fillField({css: '#deceasedDateOfDeath-day'}, createGrantOfProbateConfig.page4_deceasedDod_day);
            // await I.fillField({css: '#deceasedDateOfDeath-month'}, createGrantOfProbateConfig.page4_deceasedDod_month);
            // await I.fillField({css: '#deceasedDateOfDeath-year'}, createGrantOfProbateConfig.page4_deceasedDod_year);

            // await I.click(`#deceasedAnyOtherNames_${createGrantOfProbateConfig.page4_deceasedAnyOtherNamesYes}`);
            // await I.click('#solsDeceasedAliasNamesList > div > button');
            //await I.waitForVisible('#solsDeceasedAliasNamesList_0_SolsAliasname');
            // await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user);

            // await I.click(`#deceasedMaritalStatus-${createGrantOfProbateConfig.page4_deceasedMaritalStatus}`);
            // await I.click(`#foreignAsset_${createGrantOfProbateConfig.page4_foreignAssetYes}`);
            // await I.waitForVisible('#foreignAssetEstateValue');
            // await I.fillField('#foreignAssetEstateValue', createGrantOfProbateConfig.page4_foreignAssetEstateValue);
        }

        if (crud === 'update') {
            await expect(this.amendHeadingLocator).toBeVisible();
            await expect(this.amendDetailSelectionLocator).toBeEnabled();
            await this.amendDetailSelectionLocator.selectOption({label: `${createGrantofProbateAmendConfig.page4_list1_update_option}`});
            await this.waitForNavigationToComplete(commonConfig.continueButton);

            await expect(this.deceasedForenameLocator).toBeVisible();
            await this.deceasedForenameLocator
                .fill(createGrantofProbateAmendConfig.page4_deceasedForenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            await this.page.locator('#deceasedSurname')
                .fill(createGrantofProbateAmendConfig.page4_deceasedSurname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            await this.page.locator('#solsDeceasedAliasNamesList_0_SolsAliasname')
                .fill(createGrantofProbateAmendConfig.page4_deceasedAlias + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            await this.page.locator('#deceasedDateOfDeath-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_day_update);
            await this.page.locator('#deceasedDateOfDeath-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_month_update);
            await this.page.locator('#deceasedDateOfDeath-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_year_update);
            await this.page.locator('#deceasedDateOfBirth-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_day_update);
            await this.page.locator('#deceasedDateOfBirth-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_month_update);
            await this.page.locator('#deceasedDateOfBirth-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_year_update);

            //await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);

            // await I.waitForEnabled({css: '#selectionList'});
            // await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
            // await I.waitForNavigationToComplete(commonConfig.continueButton);

            // await I.waitForVisible('#deceasedForenames');
            // await I.fillField('#deceasedForenames', createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            // await I.fillField('#deceasedSurname', createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            // await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);

            // await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day_update);
            // await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month_update);
            // await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year_update);
            // await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day_update);
            // await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month_update);
            // await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year_update);
        }

        if (crud === 'update2orig') {
            // "reverting" update back to defaults - to enable case-match with matching case
            await expect(this.amendHeadingLocator).toBeVisible();
            await expect(this.amendDetailSelectionLocator).toBeEnabled();
            await this.amendDetailSelectionLocator.selectOption({label: `${createGrantofProbateAmendConfig.page4_list1_update_option}`});
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            await expect(this.deceasedDodDayLocator).toBeVisible();
            await this.page.locator('#deceasedDateOfDeath-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_day);
            await this.page.locator('#deceasedDateOfDeath-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_month);
            await this.page.locator('#deceasedDateOfDeath-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDod_year);
            await this.page.locator('#deceasedDateOfBirth-day')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_day);
            await this.page.locator('#deceasedDateOfBirth-month')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_month);
            await this.page.locator('#deceasedDateOfBirth-year')
                .fill(createGrantofProbateAmendConfig.page4_deceasedDob_year);
            // await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);

            // await I.waitForEnabled({css: '#selectionList'});
            // await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
            // await I.waitForNavigationToComplete(commonConfig.continueButton);

            // await I.waitForVisible('#deceasedDateOfDeath-day');
            // await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day);
            // await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month);
            // await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year);
            // await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day);
            // await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month);
            // await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year);
        }

        if (crud === 'EE') {
            await expect(this.amendHeadingLocator).toBeVisible();
            await expect(this.amendDetailSelectionLocator).toBeEnabled();
            await this.amendDetailSelectionLocator.selectOption({label: `${createGrantofProbateAmendConfig.page4_list1_update_option}`});
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            // await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);

            // await I.waitForEnabled({css: '#selectionList'});
            // await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
            // await I.waitForNavigationToComplete(commonConfig.continueButton);

            // await I.waitForVisible('#deceasedDateOfDeath-day');
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
        // await I.waitForNavigationToComplete(commonConfig.continueButton);

        if (crud === 'update' || crud === 'update2orig') {
            await this.page.locator('#ihtReferenceNumber')
                .fill(createGrantofProbateAmendConfig.page9_ihtReferenceNumber_update);
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            await expect(this.amendHeadingLocator).toBeVisible();
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            // await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber_update);
            // await I.waitForNavigationToComplete(commonConfig.continueButton);

            // await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);
            // await I.waitForNavigationToComplete(commonConfig.continueButton);
        }

        if (crud === 'EE') {
            await expect(this.amendHeadingLocator).toBeVisible();
            await this.page.locator(`#ihtFormEstateValuesCompleted_${createGrantofProbateAmendConfig.EE_ihtFormEstateValueCompletedYes}`).click();
            await this.page.locator(`#ihtFormEstate-${createGrantofProbateAmendConfig.EE_ihtFormEstate400}`).click();
            await this.waitForNavigationToComplete(commonConfig.continueButton);

            await expect(this.amendHeadingLocator).toBeVisible();
            await this.page.locator('#ihtGrossValue')
                .fill(createGrantofProbateAmendConfig.EE_ihtEstateGrossValue);
            await this.page.locator('#ihtNetValue')
                .fill(createGrantofProbateAmendConfig.EE_ihtEstateNetValue);
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }
    }

    async checkMyAnswers(nextStepName) {
        let eventSummaryPrefix = nextStepName;
        await expect(this.checkYourAnswersHeadingLocator).toBeVisible();

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await this.page.locator('#field-trigger-summary').fill(eventSummaryPrefix + eventSummaryConfig.summary);
        await this.page.locator('#field-trigger-description').fill(eventSummaryPrefix + eventSummaryConfig.comment);

        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.waitForSubmitNavigationToComplete();
    }

    async enterIhtDetails(caseProgressConfig, optionValue) {
        await expect(this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`)).toBeVisible();
        await this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`).click();
        if (optionValue === 'Yes') {
            await expect(this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`)).toBeEnabled();
            await this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`).fill(caseProgressConfig.uniqueHmrcCode);
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }
};
