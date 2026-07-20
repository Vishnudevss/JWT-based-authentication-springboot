package com.example.demo.config;

import com.example.demo.model.AuthStatus;
import com.example.demo.model.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthStatusFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication=
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {

            boolean isPartial=
                     userPrincipal.getAuthStatus()== AuthStatus.PARTIAL;

            boolean isVerify=
                     request.getRequestURI().equals("/api/v1/jwt/verify");

            boolean isLogout=
                    request.getRequestURI().equals("/logout");

            if(isPartial && !isVerify && !isLogout){

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "JWT Verification Required");

                return;
            }

        }

        filterChain.doFilter(request,response);
    }


}
