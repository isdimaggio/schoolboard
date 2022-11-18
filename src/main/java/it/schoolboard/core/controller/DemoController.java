package it.schoolboard.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
public class DemoController {

    @GetMapping("/")
    @RolesAllowed({"sb_user", "sb_admin"})
    public String index(){
        return "hello world!!";
    }

    @RolesAllowed({"sb_admin"})
    @GetMapping("/admin")
    public String admin(){
        return "admin page";
    }
}
