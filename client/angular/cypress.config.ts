import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    experimentalSessionAndOrigin: true,
    viewportHeight: 768,
    viewportWidth: 1280,
    chromeWebSecurity: false,
  },
});
