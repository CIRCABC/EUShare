import { Environment } from './interface';
export const environment: Environment = {
  production: false,
  frontend_url: 'http://localhost:8080',
  backend_url: 'http://localhost:8888',
  API_BASE_PATH: 'http://localhost:8888',
  OIDC_ISSUER: 'https://localhost:7002/cas/oauth2',
  OIDC_REDIRECTURI: window.location.origin + '/callback',
  OIDC_CLIENTID:
    '1b4w8K03ZI2xPOMZF1ZVT0Dobx9FJTRFO8hYaDMH03owPhyI3qFIW0lJHqzSvlx7kPzqXh9gTYcm3So724O9zZG-oyxudg2gCn0K2sQ4lm4EJC',
  OIDC_BACKEND_CLIENTID:
    '9IAcWq6ZHKtOqk83xfOzg8nWPlwNnPB5Uf4wnE48IUiulvUMeKdQLhs5PzHQG1Lp3Swlye7PsZli654ZmzhRoJ0-N2w0EKzVFJFzN9F09Pl7GG',
  OIDC_TOKENENDPOINT: 'https://localhost:7002/cas/oauth2/token'
};
