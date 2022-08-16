import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    experimentalSessionAndOrigin: true,
    viewportHeight: 768,
    viewportWidth: 1280,
    chromeWebSecurity: true,
    defaultCommandTimeout: 8000,
  },
  env: {
    euloginServer: 'localhost:7002',
    mailDevServer: 'localhost:1080',
  },
  retries: {
    // Configure retry attempts for `cypress run`
    // Default is 0
    runMode: 2,
    // Configure retry attempts for `cypress open`
    // Default is 0
    openMode: 0,
  },
});
