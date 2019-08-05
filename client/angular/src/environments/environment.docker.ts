import {Environment} from './interface'
export const environment: Environment = {
    production: false,
    frontend_url: 'http://localhost:8080',
    backend_url: window.location.href + ':8888',
    API_BASE_PATH: window.location.href + ':8888'
  };
  