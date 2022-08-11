package it.schoolboard.app.controller;

import it.schoolboard.app.utility.SSOUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// this controller handles all calls to the pages of the "portal"
@Controller
public class HomepageController {
    @GetMapping("/")
    @ResponseBody // respond with actual string
    public String index()
    {
        // TODO: load actual homepage model
        String a;

        SSOUser user = new SSOUser(SecurityContextHolder.getContext());

        if (user.isAuthenticated())
        {
            a = "SONO LOGGATO :)))) ";
        }
        else
        {
            a = "NON SONO LOGGATO :((((";
        }
        return "homepage <br>" +
                "<a href='/login'>login</a> <br>" +
                "<a href='/logout'>logout</a> <br>" +
                "<a href='/test'>test</a> <br>" + a;
    }

    // TODO: remove test method
    @GetMapping("/test")
    @ResponseBody // respond with actual string
    public String test()
    {
        return "area privata <br> <a href='/'>home</a>";
    }
}
