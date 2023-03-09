package eu.europa.circabc.eushare.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.UserRepository;

public class APIKeyAuthenticationManager implements AuthenticationManager {

    UserRepository userRepository;

    public APIKeyAuthenticationManager(UserRepository userRepository) {
        this.userRepository= userRepository;
    }

    @Override
      public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication != null &&
            authentication.isAuthenticated() &&
            (authentication instanceof BearerTokenAuthentication) &&
            (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal))
          return authentication;

        try {
          String apiKey = authentication.getPrincipal().toString();

          DBUser dbUser = null;

          dbUser = userRepository.findOneByApiKey(apiKey);
          if(dbUser!=null){
           authentication.setAuthenticated(true);
           return authentication;}
          else
           throw new BadCredentialsException("API-KEY is not valid");

        } catch (Exception e) {
          throw e;
        }

   
      }

    
}
