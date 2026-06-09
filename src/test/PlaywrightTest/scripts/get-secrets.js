import { populateSecrets } from "@hmcts/playwright-common";
const vaultName = "probate-aat";
const exampleEnvFilePath = "./src/test/PlaywrightTest/.env.example";
const envFilePath = "./src/test/PlaywrightTest/.env";
populateSecrets(vaultName, exampleEnvFilePath, envFilePath);
