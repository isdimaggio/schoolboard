package it.schoolboard.app.utility;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class SSOUser {
    private final SecurityContext securityContext;
    private final boolean isAuthenticated;
    private final String username;
    private final String fullName;

    private final AccessToken accessToken;

    public SSOUser(SecurityContext securityContext) {
        this.securityContext = securityContext;
        this.isAuthenticated = SBAuthChecker.checkRole(securityContext);
        this.username = securityContext.getAuthentication().getPrincipal().toString();

        if (isAuthenticated) {
            SimpleKeycloakAccount authDetails = (SimpleKeycloakAccount) securityContext.getAuthentication().getDetails();
            this.accessToken = authDetails.getKeycloakSecurityContext().getToken();
            this.fullName = accessToken.getName();
        } else {
            this.accessToken = null;
            this.fullName = null;
        }
    }

    public Authentication getAuthentication() {
        return securityContext.getAuthentication();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public String getUsername() {

        return username;
    }

    public String getUserSettingsURI(String referer) {
        // Structure of the url
        // {server}/realms/{realm}/account?referrer={client_id}&referrer_uri={referer}
        return System.getenv("KEYCLOAK_SERVER") +
                "/realms/" +
                System.getenv("KEYCLOAK_REALM") +
                "/account?referrer=" +
                System.getenv("KEYCLOAK_CLIENT_ID") +
                "&referrer_uri=" +
                referer;
    }

    public AccessToken getAccessTokenContent() {
        return accessToken;
    }

    public String getFullName() {
        return fullName;
    }
}
