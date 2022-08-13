package it.schoolboard.app.controller;

import it.schoolboard.app.utility.SSOUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// this controller handles all calls to the pages of the "portal"
@Controller
public class PortalController {
    // homepage, contains the profile selector
    @GetMapping("/")
    public String index(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/index";
    }
}
