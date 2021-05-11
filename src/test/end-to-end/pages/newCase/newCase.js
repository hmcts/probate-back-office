'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    // await I.saveScreenshot('debugNightly.png', true);
    console.info('***** HTML PRIOR TO ISSUE ********');

    const rootNode = testConfig.TestForXUI ? 'exui-root' : 'ccd-app';
    console.info(`root node: ${rootNode}`); // will indicate to us whether config flag is set to exui or ccd

    const html = await I.grabHTMLFrom({css: rootNode});
    console.info(html);
    console.info('*******************************');
    console.info('*******************************');

    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    try {
        // eslint-disable-next-line
        await I.waitForNavigationToComplete(testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator, 120);
    } catch (e) {
        // await I.saveScreenshot('debugNightlyInCatchBlock.png', true);
        console.info('***** HTML IN CATCH BLOCK ********');
        const html2 = await I.grabHTMLFrom({css: rootNode});
        console.info(html2);
        console.info('*******************************');
        console.info('*******************************');
        console.info('ERROR MESSAGE:');
        console.log(e.message);
        console.info('ERROR STACK:');
        console.log(e.stack);
        console.info('*******************************');
        console.info('*******************************');
        throw e;
    }

    // await I.saveScreenshot('debugNightlyAfterExecution.png', true);
    console.info('***** HTML AFTER NAV COMPLETE ********');
    const html3 = await I.grabHTMLFrom({css: rootNode});
    console.info(html3);
    console.info('*******************************');
    console.info('*******************************');

};
