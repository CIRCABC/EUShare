// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: '/share/webservice',
  frontend_url: '/share',
  circabc_url: 'https://circabc.development.europa.eu',
  maintenance_code: 'XXX',
  API_BASE_PATH: '/share/webservice',
  OIDC_ISSUER: 'https://eulogin:7002/cas/oauth2',
  OIDC_REDIRECTURI: `${window.location.origin}/share/callback`,
  OIDC_CLIENTID:
    'JlWW6FzZfCcI933QUaaVaWWPM8ag7NVUelCzesezV2pCOuioCjHIJRh1qbBnP3wkVQADFEarnsAjQOcEi815QTG-4y0FDGUzNApsrQw8Ga3xtG',
  OIDC_BACKEND_CLIENTID:
    'Vfen4A1b6zvjn3HLY8aNpzzGzWCuAzJhZSQXFeO1upZR8l8vzkqrX0azwFKXvsRWpgeQomzpx7lYFA6IWeNoPgUr-4y0FDGUzNApsrQw8Ga3xtG',
  OIDC_TOKENENDPOINT: 'https://eulogin:7002/cas/oauth2/token',
};
