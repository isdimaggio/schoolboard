/*
    SchoolBoard: the free and open source all-in-one platform for school
    Copyright (C) 2020-2022 Lo Mele Vittorio
    Copyright (C) 2022 Flowopia Network

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    INFO:           info@schoolboard.it
    COMMERCIAL:     commerciale@schoolboard.it
    DEVELOPEMENT:   sviluppo@schoolboard.it
 */

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
