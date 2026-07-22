package com.example.demo.service;

import com.example.demo.Dto.userDto;
import com.example.demo.model.AuthStatus;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class Authservice {


    private final AuthenticationManager authmanager;
    private final JwtService jwtservice;
    private final MyUserservice userservice;

    public Authservice(AuthenticationManager authmanager, JwtService jwtservice, MyUserservice userservice) {
        this.authmanager = authmanager;
        this.jwtservice = jwtservice;
        this.userservice = userservice;
    }


    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();


    public String verify(userDto user,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        Authentication authentication =
                authmanager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getName(),
                                user.getPassword()
                        ));

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        UserPrincipal partialuserprincipal =
                new UserPrincipal(
                        principal.getUser(),
                        AuthStatus.PARTIAL
                );

        Authentication partialAuthentication =
                new UsernamePasswordAuthenticationToken(
                        partialuserprincipal,
                        null,
                        partialuserprincipal.getAuthorities()
                );

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(partialAuthentication);
        securityContextRepository.saveContext(
                context,
                request,
                response
        );

        String token = jwtservice.generateToken(partialuserprincipal.getUsername());

        return token;
    }


    public ResponseEntity<?> verifyTokenAndSession(String token,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @AuthenticationPrincipal UserPrincipal currentUser) {

        try {
            if (currentUser == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("no active session");
            }

            if (currentUser.getAuthStatus() == AuthStatus.FULL) {
                return ResponseEntity.ok("Already verified");
            }


            if (!jwtservice.validateToken(token, currentUser)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Token");
            }

            currentUser.setAuthStatus(AuthStatus.FULL);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            currentUser.getAuthorities()
                    );

            SecurityContext context = securityContextHolderStrategy.createEmptyContext();

            context.setAuthentication(authentication);

            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );
            System.out.println("created session");
            return  ResponseEntity.ok("session upgraded successfully");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("invalid token");
        }


    }

}
