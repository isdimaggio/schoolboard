package it.schoolboard.app.controller;

import it.schoolboard.app.utility.SBAuthChecker;
import it.schoolboard.app.utility.SSOUser;
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
public class LoginController {

    private final HttpServletRequest request;

    @Autowired
    public LoginController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping("/login")
    public RedirectView login() throws ServletException {
        if (SBAuthChecker.checkRole(SecurityContextHolder.getContext())) {
            return new RedirectView("/");
        } else {
            // "ROLE_schoolboard" not in grants list, return unauthorized
            RedirectView redirectView = new RedirectView("error");
            redirectView.setStatusCode(HttpStatus.UNAUTHORIZED);
            request.logout();
            // TODO: add logging of unauthorized access
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
