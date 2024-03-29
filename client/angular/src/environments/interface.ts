export interface Environment {
  production: boolean;
  backend_url: string;
  frontend_url: string;
  circabc_url: string;
  maintenance_code: string;
  API_BASE_PATH: string;
  OIDC_ISSUER: string;
  OIDC_REDIRECTURI: string;
  OIDC_CLIENTID: string;
  OIDC_BACKEND_CLIENTID: string;
  OIDC_TOKENENDPOINT: string;
}
