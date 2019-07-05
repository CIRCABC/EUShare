// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
import {Environment} from './interface'
export const environment: Environment = {
  production: false,
  backend_url: 'http://localhost:8888',
  frontend_url: 'http://localhost:4200',
  API_BASE_PATH: 'http://localhost:8888'
};
