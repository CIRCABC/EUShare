// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: 'http://localhost:8080',
  frontend_url: '',
  circabc_url: 'https://circabc.development.europa.eu',
  maintenance_code: 'XXX',
  API_BASE_PATH: 'http://localhost:8080',
  OIDC_ISSUER: 'https://localhost:7002/cas/oauth2',
  OIDC_REDIRECTURI: `${window.location.origin}/callback`,
  OIDC_CLIENTID:
    'XQh27dQZXHsRPaCbmhOlBHbuJ9tzyosbTGYzYywdXgRgRITzRKEcZTPjzaTSTYj6zxKaVBSgI5WYy4yXbrKMVIp-U2R13UEYAiGk1MvezS9nHV',
  OIDC_BACKEND_CLIENTID:
    'RJAmmWE5qTQzImjrzn1od0tSElvyLzRWYObJnZOkNCR8xzzS1OHYpvJQ2rzuFGDwl96j7SAQ4Qv3e0c4LiK2X2G0-U2R13UEYAiGk1MvezS9nHV',
  OIDC_TOKENENDPOINT: 'https://localhost:7002/cas/oauth2/token',
};
