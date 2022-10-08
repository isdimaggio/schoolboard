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

package it.schoolboard.app.controller;

import it.schoolboard.app.utility.SBAuthChecker;
import it.schoolboard.app.utility.SSOUser;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class LoginController {

    private final HttpServletRequest request;

    @Autowired
    public LoginController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping("/login")
    public RedirectView login() throws ServletException {
        AccessToken user = new SSOUser(SecurityContextHolder.getContext()).getAccessTokenContent();
        if (SBAuthChecker.checkRole(SecurityContextHolder.getContext())) {
            log.info("Login request accepted from Id: " + user.getId() + " Username: " + user.getName());
            return new RedirectView("/");
        } else {
            // "ROLE_schoolboard" not in grants list, return unauthorized
            RedirectView redirectView = new RedirectView("error");
            redirectView.setStatusCode(HttpStatus.UNAUTHORIZED);
            request.logout();
            log.warn("Login request REJECTED from Id: " + user.getId() + " Username: " + user.getName());
            return redirectView;
        }
    }

    @GetMapping("/ssoSettings")
    public RedirectView ssoSettings(
            @RequestHeader(value = "referer") final String referer
    ) throws ServletException {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        return new RedirectView(
                user.getUserSettingsURI(referer)
        );
    }

    @GetMapping("/logout")
    public RedirectView logout() throws ServletException {
        request.logout();
        return new RedirectView("/");
    }

}
