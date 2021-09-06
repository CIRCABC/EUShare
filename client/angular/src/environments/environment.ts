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
  OIDC_REDIRECTURI: `${window.location.origin}/callback`,
  OIDC_ISSUER: 'https://ecas-mockup.cc.cec.eu.int:7002/cas/oauth2',
  OIDC_CLIENTID: 'CyLq8yleGpc7G1MmxF8la4ckmvzuxN0dodvXht89zzjqXi6geXFTIaNyfpRjAVFMisztmpvxWdXIrLqyNQvErbS-l0EvyMANoKy8j6knSKYyfa',
  OIDC_BACKEND_CLIENTID: 'VtwvhHcuGQFNUV519SdUj7ZXFztOqUqKyizqyusEHYyeq4zWWg0wlrsCH2C2k2UOdRP7BSzgl5ZCxpC8YsaRQRy-l0EvyMANoKy8j6knSKYyfa',
  OIDC_TOKENENDPOINT: 'https://ecas-mockup.cc.cec.eu.int:7002/cas/oauth2/token'
  };

