import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    experimentalSessionAndOrigin: true,
    viewportHeight: 768,
    viewportWidth: 1280,
    chromeWebSecurity: false,
  },
  env: {
    euloginServer: 'localhost:7002',
    mailDevServer: 'localhost:1080',
  },
});
