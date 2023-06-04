package net.auzy.service.user;

import net.auzy.entity.idp.IdpProvider;
import net.auzy.entity.token.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

public interface IdpRegistrationService {

    boolean produceNewUserStreamIfNotRegistered(HttpServletRequest request,
                                                IdpProvider idpProvider,
                                                TokenResponse token,
                                                JwtIssuerAuthenticationManagerResolver authenticationManagerResolver);
}
