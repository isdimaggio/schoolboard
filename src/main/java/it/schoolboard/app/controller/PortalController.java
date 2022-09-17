package it.schoolboard.app.controller;

import it.schoolboard.app.utility.SSOUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// this controller handles all calls to the pages of the "portal"
@Controller
public class PortalController {

    @Autowired
    BuildProperties buildProperties;

    // homepage, contains the profile selector or the public content
    @GetMapping("/")
    public String index(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/index";
    }

    // pages that changes the avatar of the user in the whole application
    @GetMapping("/changeAvatar")
    public String changeAvatar(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/changeAvatar";
    }

    @GetMapping("/info/privacy")
    public String privacyPolicy(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/info/privacy";
    }

    @GetMapping("/info/legalNotes")
    public String legalNotes(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/info/legalNotes";
    }

    @GetMapping("/info/cookies")
    public String cookiePolicy(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        return "pages/portal/info/cookies";
    }

    @GetMapping("/info/credits")
    public String credits(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);

        // get software version / BUILD
        model.addAttribute("buildProp", buildProperties);

        return "pages/portal/info/credits";
    }
}
