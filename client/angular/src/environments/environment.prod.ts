// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: '/webservice',
  frontend_url: '',
  circabc_url: 'https://circabc.europa.eu', 
  API_BASE_PATH: '/webservice',
  OIDC_ISSUER: 'https://ecas-mockup:7002/cas/oauth2',
  OIDC_REDIRECTURI: `${window.location.origin}/callback`,
  OIDC_CLIENTID: 'my_oidc_clientid',
  OIDC_BACKEND_CLIENTID: 'my_oidc_backend_clientid',
  OIDC_TOKENENDPOINT: 'https://ecas-mockup:7002/cas/oauth2/token',
};
