const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const {BasePage} = require ('../utility/basePage');
const applyCaveatConfig = require ('../solicitorApplyCaveat/applyCaveat/applyCaveat');
const createCaveatConfig = require('../createCaveat/createCaveatConfig');
const commonConfig = require('../common/commonConfig');
const applicationDetailsConfig = require('../solicitorApplyCaveat/applicationDetails/applicationDetails');
const completeApplicationConfig = require('../solicitorApplyCaveat/completeApplication/completeApplication');
const dateFns = require('date-fns');
const {legacyParse, convertTokens} = require('@date-fns/upgrade/v2');
const makePaymentConfig = require('../solicitorApplyProbate/makePayment/makePaymentConfig');
const postPaymentReviewTabConfig = require('../caseDetails/solicitorApplyProbate/postPaymentReviewTabConfig');
const applyProbateConfig = require('../solicitorApplyProbate/applyProbate/applyProbateConfig');

exports.SolCreateCasePage = class SolCreateCasePage extends BasePage {
    constructor(page) {
        super(page);
        this.page = page;
        this.completeApplicationSubmitButton = this.page.getByRole('button', {name: 'Close and return to case details'});
        this.serviceRequestTabLocator = this.page.getByRole('tab', {name: makePaymentConfig.paymentTab});
        this.reviewLinkLocator = this.page.getByText(makePaymentConfig.reviewLinkText);
        this.backToServiceRequestLocator = this.page.getByRole('link', {name: makePaymentConfig.backToPaymentLinkText, exact: true});
        this.payNowLinkLocator = this.page.getByRole('link', {name: makePaymentConfig.payNowLinkText, exact: true});
        this.pbaOptionLocator = this.page.locator('#pbaAccount');
        this.pbaAccountNumberLocator = this.page.locator('#pbaAccountNumber');
        this.confirmPaymentButton = this.page.getByRole('button', {name: 'Confirm payment'});
        this.serviceRequestLinkLocator = this.page.getByRole('link', {name: makePaymentConfig.serviceRequestLink, exact: true});
        this.eventHistoryTab = this.page.getByRole('tab', {name: makePaymentConfig.eventHistoryTab});
        this.caseProgressTabLocator = this.page.getByRole('tab', {name: makePaymentConfig.caseProgressTab, exact: true});
        this.postcodeLinkLocator = page.getByText(createCaveatConfig.UKpostcodeLink);
        this.solSignSot = this.page.locator(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
    }

    async applyCaveatPage1(testInfo) {
        await expect(this.page.locator('#solsCaveatEligibility')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async applyCaveatPage2(testInfo) {
        await expect(this.page.locator('#solsSolicitorFirmName')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.page.locator('#solsSolicitorFirmName').fill(applyCaveatConfig.page2_firm_name);
        await this.postcodeLinkLocator.click();
        await this.page.locator('#caveatorAddress__detailAddressLine1').fill(applyCaveatConfig.address_line1);
        await this.page.locator('#caveatorAddress__detailAddressLine2').fill(applyCaveatConfig.address_line2);
        await this.page.locator('#caveatorAddress__detailAddressLine3').fill(applyCaveatConfig.address_line3);
        await this.page.locator('#caveatorAddress__detailPostTown').fill(applyCaveatConfig.address_town);
        await this.page.locator('#caveatorAddress__detailCounty').fill(applyCaveatConfig.address_county);
        await this.page.locator('#caveatorAddress__detailPostCode').fill(applyCaveatConfig.address_postcode);
        await this.page.locator('#caveatorAddress__detailCountry').fill(applyCaveatConfig.address_country);
        await this.page.locator('#solsSolicitorAppReference').fill(applyCaveatConfig.page2_app_ref);
        await this.page.locator('#caveatorEmailAddress').fill(applyCaveatConfig.page2_caveator_email);
        await this.page.locator('#solsSolicitorPhoneNumber').fill(applyCaveatConfig.page2_phone_num);
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async cyaPage(testInfo) {
        await expect(this.page.getByText('Check your answers')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.waitForSubmitNavigationToComplete('Save and continue');
    }

    async seeEndState(endState) {
        await expect(this.page.getByText('Event History')).toBeVisible();
        await this.page.getByRole('tab', {name: 'Event History'}).focus();
        await this.page.getByRole('tab', {name: 'Event History'}).click();
        await expect(this.page.getByText(endState)).toBeVisible();
    }

    async caveatApplicationDetailsPage1(testInfo) {
        await expect(this.page.locator('#caveatorForenames')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.page.locator('#caveatorForenames').fill(applicationDetailsConfig.page1_caveator_forename);
        await this.page.locator('#caveatorSurname').fill(applicationDetailsConfig.page1_caveator_surname);
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async caveatApplicationDetailsPage2(testInfo) {
        await expect(this.page.locator('#deceasedForenames')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.page.locator('#deceasedForenames').fill(applicationDetailsConfig.page2_deceased_forename);
        await this.page.locator('#deceasedSurname').fill(applicationDetailsConfig.page2_deceased_surname);
        await this.page.locator('#deceasedDateOfDeath-day').fill(applicationDetailsConfig.page2_dateOfDeath_day);
        await this.page.locator('#deceasedDateOfDeath-month').fill(applicationDetailsConfig.page2_dateOfDeath_month);
        await this.page.locator('#deceasedDateOfDeath-year').fill(applicationDetailsConfig.page2_dateOfDeath_year);
        await this.page.locator(`#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`).focus();
        await this.page.locator(`#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`).click();
        await this.page.locator(`#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`).click();
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await this.page.waitForTimeout(testConfig.ManualDelayShort);
        }

        let idx = 0;
        /* eslint-disable no-await-in-loop */
        const keys = Object.keys(applicationDetailsConfig);
        for (let i=0; i < keys.length; i++) {
            const propName = keys[i];
            if (propName.includes('page2_alias_')) {
                await this.page.getByRole('button', {name: applicationDetailsConfig.page2_addAliasButton}).first()
                    .click();
                if (!testConfig.TestAutoDelayEnabled) {
                    // only valid for local dev where we need it to run as fast as poss to minimise
                    // lost dev time
                    await this.page.waitForTimeout(testConfig.ManualDelayShort);
                }
                await expect(this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`)).toBeVisible();
                await this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`).fill(applicationDetailsConfig[propName]);
                idx += 1;
            }
        }

        await this.postcodeLinkLocator.click();
        await this.page.locator('#deceasedAddress__detailAddressLine1').fill(applicationDetailsConfig.address_line1);
        await this.page.locator('#deceasedAddress__detailAddressLine2').fill(applicationDetailsConfig.address_line2);
        await this.page.locator('#deceasedAddress__detailAddressLine3').fill(applicationDetailsConfig.address_line3);
        await this.page.locator('#deceasedAddress__detailPostTown').fill(applicationDetailsConfig.address_town);
        await this.page.locator('#deceasedAddress__detailCounty').fill(applicationDetailsConfig.address_county);
        await this.page.locator('#deceasedAddress__detailPostCode').fill(applicationDetailsConfig.address_postcode);
        await this.page.locator('#deceasedAddress__detailCountry').fill(applicationDetailsConfig.address_country);
        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async completeCaveatApplicationPage1(testInfo) {
        await this.runAccessibilityTest(testInfo);
        await this.page.locator('#solsPBAPaymentReference').fill(completeApplicationConfig.page1_paymentReference);
        await this.page.locator('input#paymentConfirmCheckbox-paymentAcknowledgement').click();
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async completeCaveatApplicationPage2(caseRef, testInfo) {
        completeApplicationConfig.page2_notification_date = dateFns.format(legacyParse(new Date()), convertTokens('DD/MM/YYYY'));
        await expect(this.page.getByText(completeApplicationConfig.page2_waitForText)).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.page.getByText(completeApplicationConfig.page2_confirmationText)).toBeVisible();
        await expect(this.page.getByText(completeApplicationConfig.page2_app_ref)).toBeVisible();
        await expect(this.page.getByText(completeApplicationConfig.page2_notification_date_text)).toBeVisible();
        await expect(this.page.getByText(completeApplicationConfig.page2_notification_date).first()).toBeVisible();
        await this.completeApplicationSubmitButton.click();
    }

    async makeCaveatPaymentPage1(caseRef, serviceRequestTabConfig, testInfo) {
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.serviceRequestTabLocator).toBeEnabled();
        await this.serviceRequestTabLocator.click();
        await this.runAccessibilityTest(testInfo);

        for (let i = 0; i < serviceRequestTabConfig.fields.length; i++) {
            if (serviceRequestTabConfig.fields[i] && serviceRequestTabConfig.fields[i] !== '') {
                // eslint-disable-line no-await-in-loop
                await expect(this.page.getByText(serviceRequestTabConfig.fields[i])).toBeVisible();
            }
        }

        await expect(this.reviewLinkLocator).toBeVisible();
        await this.reviewLinkLocator.click();
    }

    async reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig, testInfo) {
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.serviceRequestTabLocator).toBeEnabled();
        await this.runAccessibilityTest(testInfo);
        for (let i = 0; i < serviceRequestReviewTabConfig.fields.length; i++) {
            if (serviceRequestReviewTabConfig.fields[i] && serviceRequestReviewTabConfig.fields[i] !== '') {
                // eslint-disable-line no-await-in-loop
                await expect(this.page.getByText(serviceRequestReviewTabConfig.fields[i]).first()).toBeVisible();
            }
        }

        await expect(this.page.locator('.govuk-back-link')).toBeEnabled();
        await this.backToServiceRequestLocator.click();
    }

    async makePaymentPage2(caseRef, testInfo) {
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.payNowLinkLocator).toBeVisible();
        await this.payNowLinkLocator.click();
        await expect(this.page.getByText(makePaymentConfig.page2_waitForText)).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await expect(this.pbaOptionLocator).toBeEnabled();
        await this.pbaOptionLocator.click();
        await expect(this.pbaAccountNumberLocator).toBeEnabled();
        await this.pbaAccountNumberLocator.selectOption({label: makePaymentConfig.page2_pBAANumber});
        await this.page.locator('#pbaAccountRef').fill(makePaymentConfig.page2_paymentReference);
        await this.page.locator(`//label[normalize-space()="${makePaymentConfig.paymentOptionLabel}"]`).click();
        await this.confirmPaymentButton.click();
    }

    async viewPaymentStatus(testInfo, caseRef, appType) {
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.page.getByText(makePaymentConfig.paymentStatusConfirmText)).toBeVisible();
        await expect(this.serviceRequestLinkLocator).toBeEnabled();
        await this.serviceRequestLinkLocator.click();
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.page.getByText(makePaymentConfig.paymentStatus)).toBeVisible();
        await expect(this.page.getByText(makePaymentConfig.payNowLinkText)).not.toBeVisible();
        await this.postPaymentReviewDetails(caseRef, testInfo);
        for (let i = 0; i <= 6; i++) {
            await expect(this.eventHistoryTab).toBeEnabled(); // eslint-disable-line no-await-in-loop
            await expect(this.page.getByText(caseRef).first()).toBeVisible(); // eslint-disable-line no-await-in-loop
            await this.eventHistoryTab.click(); // eslint-disable-line no-await-in-loop
            // eslint-disable-line no-await-in-loop
            const result = await this.page.getByText(makePaymentConfig.statusText).isVisible()
                .catch(() => true);
            await this.page.waitForTimeout(10); // eslint-disable-line no-await-in-loop
            if (result) {
                break;
            }
            await this.page.reload();
            // await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`); // eslint-disable-line no-await-in-loop
        }
        if (appType !== 'Caveat') {
            await expect(this.caseProgressTabLocator).toBeEnabled();
            await expect(this.page.getByText(caseRef).first()).toBeVisible();
            await this.caseProgressTabLocator.focus();
            await this.caseProgressTabLocator.click();
        }
    }

    async postPaymentReviewDetails(caseRef, testInfo) {
        await expect(this.page.getByText(caseRef).first()).toBeVisible();
        await expect(this.reviewLinkLocator).toBeVisible();
        await this.reviewLinkLocator.click();
        await expect(this.serviceRequestTabLocator).toBeEnabled();
        await this.runAccessibilityTest(testInfo);

        for (let i = 0; i < postPaymentReviewTabConfig.fields.length; i++) {
            if (postPaymentReviewTabConfig.fields[i] && postPaymentReviewTabConfig.fields[i] !== '') {
                await expect(this.page.getByText(postPaymentReviewTabConfig.fields[i]).first()).toBeVisible();
                // await I.see(postPaymentReviewTabConfig.fields[i]); // eslint-disable-line no-await-in-loop
            }
        }

        await expect(this.page.locator('.govuk-back-link')).toBeEnabled();
        await this.backToServiceRequestLocator.click();
    }

    async applyForProbatePage1(testInfo) {
        await expect(this.page.locator('#solsStartPage')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await this.waitForSubmitNavigationToComplete(commonConfig.submitButton);
        // await I.waitForElement('#solsStartPage');
        // await I.runAccessibilityTest();
        // await I.waitForNavigationToComplete(commonConfig.submitButton, true);
    }

    async applyForProbatePage2(isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false, testInfo) {
        await expect(this.page.locator('#solsApplyPage')).toBeVisible();
        await this.runAccessibilityTest(testInfo);
        await expect(this.page.locator(applyProbateConfig.page2_subheading)).toBeVisible();
        await expect(this.page.locator(applyProbateConfig.page2_probatePractionerHelp)).toBeVisible();
        await expect(this.solSignSot).toBeEnabled();
        await this.solSignSot.focus();
        await this.solSignSot.click();
        // await I.waitForElement('#solsApplyPage');
        // await I.runAccessibilityTest();
        // await I.waitForText(applyProbateConfig.page2_subheading);
        // await I.waitForText(applyProbateConfig.page2_probatePractionerHelp);

        // await I.waitForElement(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
        // await I.click(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
        /*await I.fillField('#solsForenames', applyProbateConfig.page2_sol_forename);
        await I.fillField('#solsSurname', applyProbateConfig.page2_sol_surname);

        await I.fillField('#solsSOTForenames', applyProbateConfig.page2_sol_forename);
        await I.fillField('#solsSOTSurname', applyProbateConfig.page2_sol_surname);

        if (isSolicitorNamedExecutor) {
            await I.click({css: '#solsSolicitorIsExec_Yes'});
            await I.waitForVisible({css: '#applyForProbatePageHint1'});

            if (isSolicitorApplyingExecutor) {
                await I.click({css: '#solsSolicitorIsApplying_Yes'});
                await I.waitForVisible({css: '#applyForProbatePageHint1'});
            } else {
                await I.click({css: '#solsSolicitorIsApplying_No'});
                await I.waitForVisible({css: '#solsSolicitorNotApplyingReason-PowerReserved'});
                await I.click({css: '#solsSolicitorNotApplyingReason-PowerReserved'});
            }
        } else {
            await I.click({css: '#solsSolicitorIsExec_No'});
            await I.click({css: `#solsSolicitorIsApplying_${isSolicitorApplyingExecutor ? 'Yes' : 'No'}`});
            if (isSolicitorApplyingExecutor) {
                await I.waitForVisible({css: '#applyForProbatePageHint2'});
            }
        }

        await I.fillField('#solsSolicitorFirmName', applyProbateConfig.page2_firm_name);

        await I.click(applyProbateConfig.UKpostcodeLink);
        await I.fillField('#solsSolicitorAddress__detailAddressLine1', applyProbateConfig.address_line1);
        await I.fillField('#solsSolicitorAddress__detailAddressLine2', applyProbateConfig.address_line2);
        await I.fillField('#solsSolicitorAddress__detailAddressLine3', applyProbateConfig.address_line3);
        await I.fillField('#solsSolicitorAddress__detailPostTown', applyProbateConfig.address_town);
        await I.fillField('#solsSolicitorAddress__detailCounty', applyProbateConfig.address_county);
        await I.fillField('#solsSolicitorAddress__detailPostCode', applyProbateConfig.address_postcode);
        await I.fillField('#solsSolicitorAddress__detailCountry', applyProbateConfig.address_country);

        await I.fillField('#solsSolicitorEmail', applyProbateConfig.page2_sol_email);
        await I.fillField('#solsSolicitorPhoneNumber', applyProbateConfig.page2_phone_num);
        await I.fillField('#solsSolicitorAppReference', applyProbateConfig.page2_app_ref);

        await I.waitForNavigationToComplete(commonConfig.continueButton, true);*/
    }
};
