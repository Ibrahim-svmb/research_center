package com.esmt.researchcenter.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/";
        String loginType = request.getParameter("login_type");
        boolean isParticipant = false;
        boolean isStaff = false;

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_PARTICIPANT")) isParticipant = true;
            if (role.equals("ROLE_ADMINISTRATEUR") || role.equals("ROLE_GESTIONNAIRE")) isStaff = true;
        }

        // Restriction Check
        if ("participant".equals(loginType) && !isParticipant) {
             if (isStaff) {
                 request.getSession().invalidate();
                 response.sendRedirect("/login?error=access_denied_role");
                 return;
             }
        }
        
        if ("staff".equals(loginType) && !isStaff) {
            request.getSession().invalidate();
            response.sendRedirect("/login?error=access_denied_role");
            return;
        }

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMINISTRATEUR")) {
                redirectUrl = "/admin";
                break;
            } else if (role.equals("ROLE_GESTIONNAIRE")) {
                redirectUrl = "/manager";
                break;
            } else if (role.equals("ROLE_PARTICIPANT")) {
                redirectUrl = "/participant";
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}
