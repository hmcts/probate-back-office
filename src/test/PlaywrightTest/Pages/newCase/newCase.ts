import { expect, Locator, Page } from '@playwright/test';
import caseProgressConfig from '../caseProgressStandard/caseProgressConfig.json' with { type: 'json' };
import checkYourAnswersConfig from '../checkYourAnswers/checkYourAnswersConfig.json' with { type: 'json' };
import commonConfig from '../common/commonConfig.json' with { type: 'json' };
import createCaveatConfig from '../createCaveat/createCaveatConfig.json' with { type: 'json' };
import createGrantofProbateAmendConfig from '../createGrantOfProbate/createGrantOfProbateConfig.json' with { type: 'json' };
import createGrantOfProbateConfig from '../createGrantOfProbateManual/createGrantOfProbateManualConfig.json' with { type: 'json' };
import createGrantOfProbateManualProbateManCaseConfig from '../createGrantOfProbateManualForProbateMan/createGrantOfProbateManualProbateManCaseConfig.json' with { type: 'json' };
import createWillLodgementConfig from '../createWillLodgement/createWillLodgementConfig.json' with { type: 'json' };
import eventSummaryConfig from '../eventSummary/eventSummaryConfig.json' with { type: 'json' };
import { BasePage } from '../utility/basePage.ts';
import newCaseConfig from './newCaseConfig.json' with { type: 'json' };

type CreateGrantOfProbateConfig = typeof createGrantOfProbateConfig | typeof createGrantOfProbateManualProbateManCaseConfig;
type CaseProgressConfig = typeof caseProgressConfig;

export class CreateCasePage extends BasePage {
    readonly createCasePageLocator = this.page.getByRole('link', {name: newCaseConfig.waitForText});
    readonly createCaseLocator = this.page.getByRole('link', {name: newCaseConfig.xuiCreateCaseLocator});
    readonly jurisdictionLocator = this.page.getByLabel(newCaseConfig.jurisdictionLocatorName);
    readonly caseTypeLocator = this.page.locator('#cc-case-type');
    readonly eventLocator = this.page.getByLabel(newCaseConfig.eventLocatorName);
    readonly startButtonLocator = this.page.getByRole('button', {name: newCaseConfig.startButton});
    readonly createCaveatPageLocator = this.page.getByText(createCaveatConfig.page1_waitForText);
    readonly applicationTypeLocatorName = this.page.getByLabel(newCaseConfig.applicationTypeLocatorName);
    readonly registryLocator = this.page.locator('#registryLocation');
    readonly applicationTypeLocator = this.page.locator('#applicationType');
    readonly caseTypeIdLocator = this.page.locator('#caseType');
    readonly lodgementTypeLocator = this.page.locator('#lodgementType');
    readonly createCaseCwTextLocator = this.page.getByText(createGrantOfProbateConfig.page1_waitForText);
    readonly amendCaveatPageLocator = this.page.getByText(createCaveatConfig.page1_amend_waitForText);
    readonly createCaveatPage2Locator = this.page.getByText(createCaveatConfig.page2_waitForText);
    readonly postcodeLinkLocator = this.page.getByText(createCaveatConfig.UKpostcodeLink);
    readonly amendCaveatPage2Locator = this.page.getByText(createCaveatConfig.page2_amend_waitForText);
    readonly createCaveatPage3Locator = this.page.getByText(createCaveatConfig.page3_waitForText);
    readonly amendCaveatPage3Locator = this.page.getByText(createCaveatConfig.page3_amend_waitForText);
    readonly amendCaveatPage4Locator = this.page.getByText(createCaveatConfig.page4_amend_waitForText);
    readonly primaryApplicantApplyingLocator = this.page.locator(`#primaryApplicantIsApplying_${createGrantOfProbateConfig.page1_applyingYes}`);
    readonly checkYourAnswersHeadingLocator = this.page.getByText(checkYourAnswersConfig.waitForText);
    readonly dateOfDeathTypeLocator = this.page.locator('#dateOfDeathType');
    readonly deceasedDomicileEngLocator = this.page.locator('#deceasedDomicileInEngWales_Yes');
    readonly deceasedAliasLocator = this.page.getByRole('group', {name: `${createGrantOfProbateConfig.page1_deceasedAnyOtherName}`}).getByLabel(`${createGrantOfProbateConfig.page1_deceasedAnyOtherNamesNo}`);
    readonly ihtPageWaitForTextLocator = this.page.getByRole('heading', {name: `${createGrantOfProbateConfig.EE_waitForText}`});
    readonly pcLocator = this.page.locator(`xpath=${createGrantOfProbateConfig.UKpostcodeLink}`);
    readonly pcLocator2 = this.page.locator(`xpath=${createGrantOfProbateConfig.UKpostcodeLink2}`);
    // TODO: Below doesn't exist?
    //readonly page4waitForTextLocator = this.page.locator(createGrantOfProbateConfig.page4_waitForText);
    readonly deceasedTitleLocator = this.page.locator('#boDeceasedTitle');
    readonly deceasedAddressLocator = this.page.locator('#deceasedAddress__detailAddressLine1');
    readonly deceasedAliasNameLocator = this.page.locator('#solsDeceasedAliasNamesList_0_SolsAliasname');
    readonly foreignAssetLocator = this.page.locator('#foreignAssetEstateValue');
    readonly amendHeadingLocator = this.page.getByRole('heading', {name: `${createGrantofProbateAmendConfig.page4_amend_waitForText}`});
    readonly amendDetailSelectionLocator = this.page.locator('#selectionList');
    readonly deceasedForenameLocator = this.page.locator('#deceasedForenames');
    readonly deceasedDodDayLocator = this.page.locator('#deceasedDateOfDeath-day');
    readonly createWillWaitForTextLocator = this.page.getByText(createWillLodgementConfig.page2_waitForText);
    readonly amendWillWaitForTextLocator = this.page.getByText(createWillLodgementConfig.page2_amend_waitForText);
    readonly genderLocator = this.page.locator('#deceasedGender');

    constructor(public readonly page: Page) {
        super(page);
    }

    async selectNewCase() {
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCasePageLocator).toBeVisible();
        await this.rejectCookies();
        await expect(this.createCaseLocator).toBeEnabled();
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.createCaseLocator.click();
    }

    async selectCaseTypeOptions(caseType: string, event: string) {
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.verifyPageLoad(this.createCaseLocator);
        await expect(this.createCaseLocator).toBeVisible();
        await expect(this.jurisdictionLocator).toBeEnabled();
        await this.jurisdictionLocator.selectOption({value: newCaseConfig.jurisdictionValue});
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.caseTypeLocator).toBeEnabled();
        await this.caseTypeLocator.selectOption({value: caseType});
        await expect(this.page.getByRole('option', {name: caseType}).first()).toBeHidden();
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.eventLocator).toBeEnabled();
        await this.eventLocator.selectOption({label: event});
        await expect(this.page.getByRole('option', {name: event})).toBeHidden();
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.startButtonLocator).toBeEnabled();
        await this.startButtonLocator.click();
        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
    }

    async enterCaveatPage1(crud: string) {
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

    async enterCaveatPage2(crud: string, unique_deceased_user: string) {
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
            const keys = Object.keys(createCaveatConfig);
            let addNewButtonLocator: Locator;
            for (let i=0; i < keys.length; i++) {
                const propName = keys[i];
                if (idx === 0) {
                    addNewButtonLocator = this.page.getByRole('button', {name: createCaveatConfig.page2_addAliasButton}).first();
                } else {
                    addNewButtonLocator = this.page.getByRole('button', {name: createCaveatConfig.page2_addAliasButton}).nth(1);
                }

                if (propName.includes('page2_alias_')) {
                    await addNewButtonLocator.click();
                    /*if (!testConfig.TestAutoDelayEnabled) {
                        await this.page.waitForTimeout(testConfig.ManualDelayShort); // implicit wait needed here
                    }*/
                    await expect(this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`)).toBeEnabled();
                    await this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`).fill(createCaveatConfig[propName]);
                    idx += 1;
                }
            }

            await this.postcodeLinkLocator.focus();
            await this.postcodeLinkLocator.click();
            /*if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayShort); // implicit wait needed here
            }*/
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

    async enterCaveatPage3(crud: string) {
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

            await this.page.locator('#caveatorForenames').fill(createCaveatConfig.page3_caveator_forenames_update);
            await this.page.locator('#caveatorSurname').fill(createCaveatConfig.page3_caveator_surname_update);
            await this.waitForNavigationToComplete(commonConfig.submitButton);
        }

    }

    async enterCaveatPage4(crud: string) {
        if (crud === 'update') {
            await expect(this.amendCaveatPage4Locator).toBeVisible();
            await expect(this.page.locator('#expiryDate-day')).toBeEnabled();

            // TODO: These page4 values do not exist
            //await this.page.locator('#expiryDate-day').fill(createCaveatConfig.page4_caveatExpiryDate_day_update);
            //await this.page.locator('#expiryDate-month').fill(createCaveatConfig.page4_caveatExpiryDate_month_update);
            //await this.page.locator('#expiryDate-year').fill(createCaveatConfig.page4_caveatExpiryDate_year_update);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbateManualPage1(crud: string, createConfig: CreateGrantOfProbateConfig, unique_deceased_user: string = Date.now().toString(), deceasedDODYear?: string) {
        if (crud === 'create') {
            await expect(this.createCaseCwTextLocator)
                .toBeVisible();
            await expect(this.page.locator('#registryLocation')).toBeEnabled();
            await this.registryLocator.selectOption({label: createConfig.page1_list1_registry_location});
            await this.applicationTypeLocator.selectOption({label: createConfig.page1_list2_application_type});

            await this.page.locator('#applicationSubmittedDate-day')
                .fill(createConfig.page1_applicationSubmittedDate_day);
            await this.page.locator('#applicationSubmittedDate-month')
                .fill(createConfig.page1_applicationSubmittedDate_month);
            await this.page.locator('#applicationSubmittedDate-year')
                .fill(createConfig.page1_applicationSubmittedDate_year);

            await this.caseTypeIdLocator.selectOption({label: createConfig.page1_list3_case_type});

            await this.page.locator('#extraCopiesOfGrant')
                .fill(createConfig.page1_extraCopiesOfGrant);
            await this.page.locator('#outsideUKGrantCopies')
                .fill(createConfig.page1_outsideUKGrantCopies);

            await expect(this.createCaseCwTextLocator).toBeVisible();
            await this.page.locator('#primaryApplicantForenames')
                .fill(createConfig.page1_firstnames);
            await this.page.locator('#primaryApplicantSurname')
                .fill(createConfig.page1_lastnames);
            await this.primaryApplicantApplyingLocator.click();
            await this.page.locator('#primaryApplicantEmailAddress')
                .fill(createConfig.page1_email);

            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }*/

            await this.pcLocator.click();
            await expect(this.page.locator('#primaryApplicantAddress__detailAddressLine1')).toBeVisible();
            await this.page.locator('#primaryApplicantAddress__detailAddressLine1')
                .fill(createConfig.address_line1);
            await this.page.locator('#primaryApplicantAddress__detailAddressLine2')
                .fill(createConfig.address_line2);
            await this.page.locator('#primaryApplicantAddress__detailAddressLine3')
                .fill(createConfig.address_line3);
            await this.page.locator('#primaryApplicantAddress__detailPostTown')
                .fill(createConfig.address_town);
            await this.page.locator('#primaryApplicantAddress__detailCounty')
                .fill(createConfig.address_county);
            await this.page.locator('#primaryApplicantAddress__detailPostCode')
                .fill(createConfig.address_postcode);
            await this.page.locator('#primaryApplicantAddress__detailCountry')
                .fill(createConfig.address_country);

            await this.page.locator(`#otherExecutorExists_${createGrantOfProbateConfig.page1_otherExecutorExistsNo}`).click();
            await this.page.locator('#boDeceasedTitle')
                .fill(createConfig.page1_bo_deceasedTitle);
            if (createConfig === createGrantOfProbateConfig) {
                await this.page.locator('#deceasedForenames')
                    .fill(createConfig.page1_deceasedForenames + '_' + unique_deceased_user);
                await this.page.locator('#deceasedSurname')
                    .fill(createConfig.page1_deceasedSurname + '_' + unique_deceased_user);
            } else {
                await this.page.locator('#deceasedForenames')
                    .fill(createConfig.page1_deceasedForenames);
                await this.page.locator('#deceasedSurname')
                    .fill(createConfig.page1_deceasedSurname);
            }

            await this.page.locator('#boDeceasedHonours')
                .fill(createConfig.page1_bo_deceasedHonours);

            await expect(this.pcLocator2).toBeVisible();
            await this.pcLocator2.click();

            await expect(this.page.locator('#deceasedAddress__detailAddressLine1')).toBeVisible();
            await this.page.locator('#deceasedAddress__detailAddressLine1')
                .fill(createConfig.address_line1);
            await this.page.locator('#deceasedAddress__detailAddressLine2')
                .fill(createConfig.address_line2);
            await this.page.locator('#deceasedAddress__detailAddressLine3')
                .fill(createConfig.address_line3);
            await this.page.locator('#deceasedAddress__detailPostTown')
                .fill(createConfig.address_town);
            await this.page.locator('#deceasedAddress__detailCounty')
                .fill(createConfig.address_county);
            await this.page.locator('#deceasedAddress__detailPostCode')
                .fill(createConfig.address_postcode);
            await this.page.locator('#deceasedAddress__detailCountry')
                .fill(createConfig.address_country);

            await expect(this.dateOfDeathTypeLocator).toBeEnabled();
            await this.dateOfDeathTypeLocator.selectOption({label: `${createConfig.page1_dateOfDeathType}`});
            await this.page.locator('#deceasedDateOfBirth-day')
                .fill(createConfig.page1_deceasedDob_day);
            await this.page.locator('#deceasedDateOfBirth-month')
                .fill(createConfig.page1_deceasedDob_month);
            await this.page.locator('#deceasedDateOfBirth-year')
                .fill(createConfig.page1_deceasedDob_year);
            await this.page.locator('#deceasedDateOfDeath-day')
                .fill(createConfig.page1_deceasedDod_day);
            await this.page.locator('#deceasedDateOfDeath-month')
                .fill(createConfig.page1_deceasedDod_month);
            if (createConfig === createGrantOfProbateConfig && deceasedDODYear) {
                await this.page.locator('#deceasedDateOfDeath-year')
                    .fill(deceasedDODYear);
            } else {
                await this.page.locator('#deceasedDateOfDeath-year')
                    .fill(createConfig.page1_deceasedDod_year);
            }

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

    async enterGrantOfProbateManualPage2(crud: string) {
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

    async enterGrantOfProbateManualPage3(crud: string, createConfig: CreateGrantOfProbateConfig) {
        if (crud === 'create') {
            await expect(this.createCaseCwTextLocator).toBeVisible();

            await this.page.locator('#ihtGrossValue')
                .fill(createConfig.EE_ihtEstateGrossValue);
            await this.page.locator('#ihtNetValue')
                .fill(createConfig.EE_ihtEstateNetValue);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterGrantOfProbatePage4(crud: string, unique_deceased_user: string = Date.now().toString()) {
        if (crud === 'create') {
            // TODO: This doesn't exist
            //await expect(this.page4waitForTextLocator).toBeVisible();
            await expect(this.deceasedTitleLocator).toBeEnabled();
            await this.deceasedTitleLocator
                .fill(createGrantofProbateAmendConfig.page4_bo_deceasedTitle);
            await this.page.locator('#deceasedForenames')
                .fill(createGrantofProbateAmendConfig.page4_deceasedForenames + '_' + unique_deceased_user);
            await this.page.locator('#deceasedSurname')
                .fill(createGrantofProbateAmendConfig.page4_deceasedSurname + '_' + unique_deceased_user);
            await this.page.locator('#boDeceasedHonours')
                .fill(createGrantofProbateAmendConfig.page4_bo_deceasedHonours);

            await expect(this.pcLocator).toBeVisible();
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
        }

        if (crud === 'EE') {
            await expect(this.amendHeadingLocator).toBeVisible();
            await expect(this.amendDetailSelectionLocator).toBeEnabled();
            await this.amendDetailSelectionLocator.selectOption({label: `${createGrantofProbateAmendConfig.page4_list1_update_option}`});
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }

        await this.waitForNavigationToComplete(commonConfig.continueButton);
        if (crud === 'update' || crud === 'update2orig') {
            await this.page.locator('#ihtReferenceNumber')
                .fill(createGrantofProbateAmendConfig.page9_ihtReferenceNumber_update);
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            await expect(this.amendHeadingLocator).toBeVisible();
            await this.waitForNavigationToComplete(commonConfig.continueButton);
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

    async checkMyAnswers(nextStepName: string) {
        let eventSummaryPrefix = nextStepName;
        await expect(this.checkYourAnswersHeadingLocator).toBeVisible();

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await this.page.locator('#field-trigger-summary').fill(eventSummaryPrefix + eventSummaryConfig.summary);
        await this.page.locator('#field-trigger-description').fill(eventSummaryPrefix + eventSummaryConfig.comment);

        // await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.waitForNavigationToComplete(commonConfig.submitButton);
    }

    async enterIhtDetails(caseProgressConfig: CaseProgressConfig, optionValue: string) {
        await expect(this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`)).toBeVisible();
        await this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`).click();
        if (optionValue === 'Yes') {
            await expect(this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`)).toBeEnabled();
            await this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`).fill(caseProgressConfig.uniqueHmrcCode);
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterWillLodgementPage1(crud: string) {
        if (crud === 'create') {
            // await this.page.waitForTimeout(testConfig.WaitForTextTimeout);
            await expect(this.applicationTypeLocator).toBeEnabled();
            await this.applicationTypeLocator.selectOption({label: createWillLodgementConfig.page1_list1_application_type});
            await expect(this.registryLocator).toBeVisible();
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({label: createWillLodgementConfig.page1_list2_registry_location});
            await expect(this.lodgementTypeLocator).toBeVisible();
            await expect(this.lodgementTypeLocator).toBeEnabled();
            await this.lodgementTypeLocator.selectOption({label: createWillLodgementConfig.page1_list3_lodgement_type});

            await this.page.locator('#lodgedDate-day').fill(createWillLodgementConfig.page1_lodgedDate_day);
            await this.page.locator('#lodgedDate-month').fill(createWillLodgementConfig.page1_lodgedDate_month);
            await this.page.locator('#lodgedDate-year').fill(createWillLodgementConfig.page1_lodgedDate_year);

            await this.page.locator('#willDate-day').fill(createWillLodgementConfig.page1_willDate_day);
            await this.page.locator('#willDate-month').fill(createWillLodgementConfig.page1_willDate_month);
            await this.page.locator('#willDate-year').fill(createWillLodgementConfig.page1_willDate_year);

            await this.page.locator('#codicilDate-day').fill(createWillLodgementConfig.page1_codicilDate_day);
            await this.page.locator('#codicilDate-month').fill(createWillLodgementConfig.page1_codicilDate_month);
            await this.page.locator('#codicilDate-year').fill(createWillLodgementConfig.page1_codicilDate_year);

            await this.page.locator('#numberOfCodicils').fill(createWillLodgementConfig.page1_numberOfCodicils);
            await this.page.locator(`#jointWill_${createWillLodgementConfig.page1_jointWill}`).click();
        }

        if (crud === 'update') {
            // await this.page.waitForTimeout(testConfig.WaitForTextTimeout);
            await expect(this.registryLocator).toBeVisible();
            await expect(this.registryLocator).toBeEnabled();
            await this.registryLocator.selectOption({label: createWillLodgementConfig.page1_list2_registry_location_update});
            await expect(this.lodgementTypeLocator).toBeVisible();
            await expect(this.lodgementTypeLocator).toBeEnabled();
            await this.lodgementTypeLocator.selectOption({label: createWillLodgementConfig.page1_list3_lodgement_type_update});
            await this.page.locator('#lodgedDate-day').fill(createWillLodgementConfig.page1_lodgedDate_day_update);
            await this.page.locator('#lodgedDate-month').fill(createWillLodgementConfig.page1_lodgedDate_month_update);
            await this.page.locator('#lodgedDate-year').fill(createWillLodgementConfig.page1_lodgedDate_year_update);

            await this.page.locator('#numberOfCodicils').fill(createWillLodgementConfig.page1_numberOfCodicils_update);
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterWillLodgementPage2(crud: string, unique_deceased_user: string = Date.now().toString()) {
        if (crud === 'create') {
            await expect(this.createWillWaitForTextLocator).toBeVisible();

            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }*/
            await this.page.locator('#deceasedForenames').fill(createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }*/

            await this.page.locator('#deceasedSurname').fill(createWillLodgementConfig.page2_surname + '_' + unique_deceased_user);
            await this.genderLocator.selectOption({label: createWillLodgementConfig.page2_gender});
            await this.page.locator('#deceasedDateOfBirth-day').fill(createWillLodgementConfig.page2_dateOfBirth_day);
            await this.page.locator('#deceasedDateOfBirth-month').fill(createWillLodgementConfig.page2_dateOfBirth_month);
            await this.page.locator('#deceasedDateOfBirth-year').fill(createWillLodgementConfig.page2_dateOfBirth_year);
            await this.page.locator('#deceasedDateOfDeath-day').fill(createWillLodgementConfig.page2_dateOfDeath_day);
            await this.page.locator('#deceasedDateOfDeath-month').fill(createWillLodgementConfig.page2_dateOfDeath_month);
            await this.page.locator('#deceasedDateOfDeath-year').fill(createWillLodgementConfig.page2_dateOfDeath_year);
            await this.page.locator('#deceasedTypeOfDeath').selectOption({label: createWillLodgementConfig.page2_typeOfDeath});
            // await this.page.waitForTimeout(testConfig.ManualDelayShort);
            await expect(this.page.locator(`#deceasedAnyOtherNames_${createWillLodgementConfig.page2_hasAliasYes}`)).toBeEnabled();
            await this.page.locator(`#deceasedAnyOtherNames_${createWillLodgementConfig.page2_hasAliasYes}`).click();
            await this.page.locator(`#deceasedAnyOtherNames_${createWillLodgementConfig.page2_hasAliasYes}`).click();

            let idx = 0;
            const keys = Object.keys(createWillLodgementConfig);
            for (let i=0; i < keys.length; i++) {
                const propName = keys[i];
                if (propName.includes('page2_alias_')) {
                    await this.page.locator(createWillLodgementConfig.page2_addAliasButton)
                        .first()
                        .click();
                    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
                    const deceasedNameListlocator = this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`);
                    /*if (!testConfig.TestAutoDelayEnabled) {
                        // only valid for local dev where we need it to run as fast as poss to minimise
                        // lost dev time
                        await this.page.waitForTimeout(testConfig.ManualDelayShort);
                    }*/
                    await expect(deceasedNameListlocator).toBeVisible();
                    await deceasedNameListlocator.fill(createWillLodgementConfig[propName]);
                    idx += 1;
                }
            }
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }*/
            await this.page.locator('#deceasedFullAliasNameList_0_FullAliasName').fill(createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user);

            await this.pcLocator2.click();
            await this.page.locator('#deceasedAddress__detailAddressLine1').fill(createWillLodgementConfig.address_line1);
            await this.page.locator('#deceasedAddress__detailAddressLine2').fill(createWillLodgementConfig.address_line2);
            await this.page.locator('#deceasedAddress__detailAddressLine3').fill(createWillLodgementConfig.address_line3);
            await this.page.locator('#deceasedAddress__detailPostTown').fill(createWillLodgementConfig.address_town);
            await this.page.locator('#deceasedAddress__detailCounty').fill(createWillLodgementConfig.address_county);
            await this.page.locator('#deceasedAddress__detailPostCode').fill(createWillLodgementConfig.address_postcode);
            await this.page.locator('#deceasedAddress__detailCountry').fill(createWillLodgementConfig.address_country);
            await this.page.locator('#deceasedEmailAddress').fill(createWillLodgementConfig.page2_email);
        }

        if (crud === 'update') {
            await expect(this.amendWillWaitForTextLocator).toBeVisible();

            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }*/
            await this.page.locator('#deceasedForenames').fill(createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/

            await this.page.locator('#deceasedSurname').fill(createWillLodgementConfig.page2_surname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/
            await this.page.locator('#deceasedFullAliasNameList_0_FullAliasName').fill(createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
            await this.page.locator('#deceasedDateOfDeath-day').fill(createWillLodgementConfig.page2_dateOfDeath_day_update);
            await this.page.locator('#deceasedDateOfDeath-month').fill(createWillLodgementConfig.page2_dateOfDeath_month_update);
            await this.page.locator('#deceasedDateOfDeath-year').fill(createWillLodgementConfig.page2_dateOfDeath_year_update);
            await this.page.locator('#deceasedDateOfBirth-day').fill(createWillLodgementConfig.page2_dateOfBirth_day_update);
            await this.page.locator('#deceasedDateOfBirth-month').fill(createWillLodgementConfig.page2_dateOfBirth_month_update);
            await this.page.locator('#deceasedDateOfBirth-year').fill(createWillLodgementConfig.page2_dateOfBirth_year_update);
        }

        if (crud === 'update2orig') {

            // "reverting" update back to defaults - to enable case-match with matching case
            await this.waitForNavigationToComplete(commonConfig.continueButton);
            await expect(this.amendWillWaitForTextLocator).toBeVisible();
            await this.page.locator('#deceasedDateOfDeath-day').fill(createWillLodgementConfig.page2_dateOfDeath_day);
            await this.page.locator('#deceasedDateOfDeath-month').fill(createWillLodgementConfig.page2_dateOfDeath_month);
            await this.page.locator('#deceasedDateOfDeath-year').fill(createWillLodgementConfig.page2_dateOfDeath_year);
            await this.page.locator('#deceasedDateOfBirth-day').fill(createWillLodgementConfig.page2_dateOfBirth_day);
            await this.page.locator('#deceasedDateOfBirth-month').fill(createWillLodgementConfig.page2_dateOfBirth_month);
            await this.page.locator('#deceasedDateOfBirth-year').fill(createWillLodgementConfig.page2_dateOfBirth_year);
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async enterWillLodgementPage3(crud: string) {
        const index = 0;
        /* eslint prefer-const: 0 */
        let executorFieldList = [];
        let additionalExecutorFieldList = [];

        Object.keys(createWillLodgementConfig).forEach(function (value) {
            // const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
            if (value.includes(`page3_executor${index}`)) {
                executorFieldList.push(value);
            }
        });

        if (crud === 'create') {
            await expect(this.page.getByText(createWillLodgementConfig.page3_waitForText)).toBeVisible();
            await this.page.locator('#executorTitle').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/
            await this.page.locator('#executorForenames').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames`)]]);
            // await I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames`)]]);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/
            await this.page.locator('#executorSurname').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname`)]]);
            await this.page.locator('#executorEmailAddress').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);
            await this.postcodeLinkLocator.click();
            await this.page.locator('#executorAddress__detailAddressLine1').fill(createWillLodgementConfig.address_line1);
            await this.page.locator('#executorAddress__detailAddressLine2').fill(createWillLodgementConfig.address_line2);
            await this.page.locator('#executorAddress__detailAddressLine3').fill(createWillLodgementConfig.address_line3);
            await this.page.locator('#executorAddress__detailPostTown').fill(createWillLodgementConfig.address_town);
            await this.page.locator('#executorAddress__detailCounty').fill(createWillLodgementConfig.address_county);
            await this.page.locator('#executorAddress__detailPostCode').fill(createWillLodgementConfig.address_postcode);
            await this.page.locator('#executorAddress__detailCountry').fill(createWillLodgementConfig.address_country);

            Object.keys(createWillLodgementConfig).forEach(function (value) {
                if (value.includes('page3_additional_executor')) {
                    additionalExecutorFieldList.push(value);
                }
            });

            await this.page.locator(createWillLodgementConfig.page3_addExecutorButton).click();
            await expect(this.page.locator(`#additionalExecutorList_${index}_executorForenames`)).toBeEnabled();
            // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            await this.page.locator(`#additionalExecutorList_${index}_executorTitle`).fill(createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/

            await this.page.locator(`#additionalExecutorList_${index}_executorForenames`).fill(createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_forenames`)]]);
            await this.page.locator(`#additionalExecutorList_${index}_executorSurname`).fill(createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_surname`)]]);
            await this.postcodeLinkLocator.click();
            await expect(this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailAddressLine1`)).toBeVisible();
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailAddressLine1`).fill(createWillLodgementConfig.address_line1);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailAddressLine2`).fill(createWillLodgementConfig.address_line2);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailAddressLine3`).fill(createWillLodgementConfig.address_line3);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailPostTown`).fill(createWillLodgementConfig.address_town);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailCounty`).fill(createWillLodgementConfig.address_county);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailPostCode`).fill(createWillLodgementConfig.address_postcode);
            await this.page.locator(`#additionalExecutorList_${index}_executorAddress__detailCountry`).fill(createWillLodgementConfig.address_country);
            await this.page.locator(`#additionalExecutorList_${index}_executorEmailAddress`).fill(createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);
        }

        if (crud === 'update') {
            await expect(this.page.getByText(createWillLodgementConfig.page3_amend_waitForText)).toBeVisible();
            await this.page.locator('#executorTitle').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title_update`)]]);

            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/
            await this.page.locator('#executorForenames').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames_update`)]]);
            /*if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }*/
            await this.page.locator('#executorSurname').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname_update`)]]);
            await this.page.locator('#executorEmailAddress').fill(createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email_update`)]]);
        }
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }
};
