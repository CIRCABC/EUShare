// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  backend_url: 'http://localhost:8888',
  frontend_url: 'http://localhost:4200',
  API_BASE_PATH: 'http://localhost:8888',
  OIDC_ISSUER: 'https://localhost:7002/cas/oauth2',
  OIDC_REDIRECTURI: window.location.origin + '/callback',
  OIDC_CLIENTID: '1b4w8K03ZI2xPOMZF1ZVT0Dobx9FJTRFO8hYaDMH03owPhyI3qFIW0lJHqzSvlx7kPzqXh9gTYcm3So724O9zZG-oyxudg2gCn0K2sQ4lm4EJC',
  OIDC_BACKEND_CLIENTID: '9IAcWq6ZHKtOqk83xfOzg8nWPlwNnPB5Uf4wnE48IUiulvUMeKdQLhs5PzHQG1Lp3Swlye7PsZli654ZmzhRoJ0-N2w0EKzVFJFzN9F09Pl7GG',
  OIDC_TOKENENDPOINT: 'https://localhost:7002/cas/oauth2/token'
};
