// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: '/share/webservice',
  circabc_url: 'https://circabc.europa.eu', 
  API_BASE_PATH: '/share/webservice',
  OIDC_ISSUER: 'https://eulogin:7002/cas/oauth2',
  OIDC_REDIRECTURI: `${window.location.origin}/share/callback`,
  OIDC_CLIENTID:
    'qeBCzrSrDsizahesBX4QY30a6QYXLj8e1vRTIbqav1ajjnDjSJCPRdcZRwiIITzcbzITc7i8i8y6VZ5CDANzoL2-4y0FDGUzNApE4Hnya956ue',
  OIDC_BACKEND_CLIENTID:
    '6QCA7zVJsPSmaZfjsZ5QsjIxpXBmzadfzMjRX5Gc0WFywHmF392po2PjVRgzvWKo7n5qo3c5hyPxFEuItUdM3lK-4y0FDGUzNApE4Hnya956ue',
  OIDC_TOKENENDPOINT: 'https://eulogin:7002/cas/oauth2/token',

};
