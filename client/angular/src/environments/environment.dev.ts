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
    'rHT4oyRzuxP8zeFS1BNHbL7ywmUYmXBhKVSv7xEI9VanNIvkPSl5c9efm299nX62fG6at6nzelyhE7kO3GbZBPG-4y0FDGUzNApELWPIBzHLNzG',
  OIDC_BACKEND_CLIENTID:
    'HcoTkGJi0PxIzzJosaxC9iQmf5FcCtYzMJjRbTcYOGNe2o2ixouUVZWjR7zI0qe7mEL5BGwzMVJvPqCeePRtwdG-4y0FDGUzNApELWPIBzHLNzG',
  OIDC_TOKENENDPOINT: 'https://eulogin:7002/cas/oauth2/token',
};
