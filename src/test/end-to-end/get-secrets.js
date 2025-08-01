import {populateSecrets} from '@hmcts/playwright-common';
const vaultName = 'probate-aat';
const exampleEnvFilePath = './src/test/end-to-end/.env.example';
const envFilePath = './src/test/end-to-end/.env';
populateSecrets(vaultName, exampleEnvFilePath, envFilePath);
