package it.schoolboard.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@RestController
public class HomepageController {

    private final HttpServletRequest request;

    @Autowired
    public HomepageController(HttpServletRequest request){
        this.request = request;
    }

    @GetMapping("/")
    public String index(){
        return "fai finta che sta la home di schoolboard <br>" +
                "<a href='/login'>login</a> <br>" +
                "<a href='/logout'>logout</a> <br>" +
                "<a href='/test'>test</a> <br>";
    }

    @GetMapping("/test")
    public String test(){
        return "SONO LOGGATO!!!! area privata!!!! <br> <a href='/'>home</a>";
    }

    @GetMapping("/login")
    public RedirectView login() throws ServletException {
        return new RedirectView("/");
    }


    @GetMapping("/logout")
    public RedirectView logout() throws ServletException {
        request.logout();
        return new RedirectView("/");
    }

}
