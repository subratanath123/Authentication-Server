package net.auzy.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationUtils {

    public static String getLoggedInEmail() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authenticationToken.getName();
    }

}
