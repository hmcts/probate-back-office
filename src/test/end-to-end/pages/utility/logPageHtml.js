'use strict';

const testConfig = require('src/test/config');

module.exports = async function (isPreIssue = false, err = null) {

    const I = this;
    let issueMsg;
    if (isPreIssue) {
        issueMsg = 'HTML PRIOR TO ISSUE';
    } else if (err) {
        issueMsg = 'HTML IN CATCH BLOCK';
    } else {
        issueMsg = 'HTML AFTER ISSUE';
    }

    // eslint-disable-next-line
    console.info(`******* ${issueMsg} ********`);

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

    if (err) {
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
    }
};
