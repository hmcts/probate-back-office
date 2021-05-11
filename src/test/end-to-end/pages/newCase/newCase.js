'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    /* eslint-disable-no-console */

    // await I.saveScreenshot('debugNightly.png', true);
    // eslint-disable-next-line
    console.info('***** HTML PRIOR TO ISSUE ********');

    const rootNode = testConfig.TestForXUI ? 'exui-root' : 'ccd-app';
    // eslint-disable-next-line
    console.info(`root node: ${rootNode}`); // will indicate to us whether config flag is set to exui or ccd

    const html = await I.grabHTMLFrom({css: rootNode});
    // eslint-disable-next-line
    console.info(html);
    // eslint-disable-next-line
    console.info('*******************************');
    // eslint-disable-next-line
    console.info('*******************************');

    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    try {
        // eslint-disable-next-line
        await I.waitForNavigationToComplete(testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator, 120);
    } catch (e) {
        // await I.saveScreenshot('debugNightlyInCatchBlock.png', true);
        // eslint-disable-next-line
        console.info('***** HTML IN CATCH BLOCK ********');
        const html2 = await I.grabHTMLFrom({css: rootNode});
        // eslint-disable-next-line
        console.info(html2);
        // eslint-disable-next-line
        console.info('*******************************');
        // eslint-disable-next-line
        console.info('*******************************');
        // eslint-disable-next-line
        console.info('ERROR MESSAGE:');
        // eslint-disable-next-line
        console.log(e.message);
        // eslint-disable-next-line
        console.info('ERROR STACK:');
        // eslint-disable-next-line
        console.log(e.stack);
        // eslint-disable-next-line
        console.info('*******************************');
        // eslint-disable-next-line
        console.info('*******************************');
        throw e;
    }

    // await I.saveScreenshot('debugNightlyAfterExecution.png', true);
    // eslint-disable-next-line
    console.info('***** HTML AFTER NAV COMPLETE ********');
    const html3 = await I.grabHTMLFrom({css: rootNode});
    // eslint-disable-next-line
    console.info(html3);
    // eslint-disable-next-line
    console.info('*******************************');
    // eslint-disable-next-line
    console.info('*******************************');
};
