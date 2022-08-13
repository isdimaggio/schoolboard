package it.schoolboard.app.utility;

import org.springframework.security.core.context.SecurityContext;

public class SBAuthChecker {

    public static boolean checkRole(SecurityContext securityContext) {
        boolean flag = false;
        for (Object role : securityContext.getAuthentication().getAuthorities().toArray()) {
            if (role.toString().equals("ROLE_schoolboard")) {
                flag = true;
            }
        }
        return flag;
    }

}
