// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: 'http://localhost:8888',
  frontend_url: 'http://localhost:8080',
  API_BASE_PATH: 'http://localhost:8888',
  OIDC_ISSUER: 'https://localhost:7002/cas/oauth2',
  OIDC_REDIRECTURI: window.location.origin + '/callback',
  OIDC_CLIENTID:
    'SUaZzHzotYc0aWZwRNH8DsNdVF0RdzvszanVEMzVlUuQSpy8WEylWDiKTrspJtg7fnbizPObS5HObgpIVDNWL0rG-PnCd3ypAN98azctE7ydwH9',
  OIDC_BACKEND_CLIENTID:
    'jJFw8RBTrvm8LFdWe6nINhPPOg7ygzcfihddYGsWM3EbE391A9R3iezpeUzygoQUwEUDoLBcERD86CWx5WNKZaW-PnCd3ypAN98azctE7ydwH9',
  OIDC_TOKENENDPOINT: 'https://localhost:7002/cas/oauth2/token',
};
