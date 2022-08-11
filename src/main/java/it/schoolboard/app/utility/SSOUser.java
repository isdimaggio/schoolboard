package it.schoolboard.app.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class SSOUser
{

    private SecurityContext securityContext;

    public SSOUser(org.springframework.security.core.context.SecurityContext securityContext)
    {
        this.securityContext = securityContext;
    }

    public boolean isAuthenticated()
    {
        return SBAuthChecker.checkRole(securityContext);
    }

    public Authentication getAuthentication()
    {
        return securityContext.getAuthentication();
    }

}
