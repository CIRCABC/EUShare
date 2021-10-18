// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  frontend_url: '/',
  backend_url: '/webservice',
  API_BASE_PATH: '/webservice',
  OIDC_ISSUER: 'https://eulogin:7002/cas/oauth2',
  OIDC_REDIRECTURI: `${window.location.origin}/callback`,
  OIDC_CLIENTID: 'wr7KvlzqBR5iySOs4QYXgJYjw1Zu6E35toDsGdza0ViwqHMZlnstwwilEgkzazqm6ssLpzfKKHog8u4sUnK8DEH-4y0FDGUzNAp3Z2nrB01jCK',
  OIDC_BACKEND_CLIENTID: 'LTUzzKUROQlIqzgkCIHZ9czRHTWh3UXjzRgQzRQNrIA01AVCvLZ2wM5ApdezdUkuOcOJIZUpzTLsUEofwajHVbyk-4y0FDGUzNAp3Z2nrB01jCK',
  OIDC_TOKENENDPOINT: 'https://eulogin:7002/cas/oauth2/token'
  };
