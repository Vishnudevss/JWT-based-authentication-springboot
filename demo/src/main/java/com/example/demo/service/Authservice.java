package com.example.demo.service;

import com.example.demo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class Authservice {


     private  final AuthenticationManager authmanager;
     private final JwtService jwtservice;
     private final MyUserservice userservice;

    public Authservice(AuthenticationManager authmanager, JwtService jwtservice, MyUserservice userservice) {
        this.authmanager = authmanager;
        this.jwtservice = jwtservice;
        this.userservice = userservice;
    }


    private final SecurityContextRepository securityContextRepository=
            new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy=
            SecurityContextHolder.getContextHolderStrategy();


    public String verify(User user) {

        Authentication authentication=
                authmanager.authenticate
                        (new UsernamePasswordAuthenticationToken(user.getName(),user.getPassword()));

        if( authentication.isAuthenticated()){
            return jwtservice.generateToken(user.getName());
        }

        return "failure";
    }

    public ResponseEntity<?> verifyTokenAndSession(String token,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        try{
            String username=jwtservice.extractUserName(token);

            UserDetails userDetails=userservice.loadUserByUsername(username);

            if(!jwtservice.validateToken(token,userDetails)){
                return  ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("invalid token");
            }

            Authentication authentication=
                    new UsernamePasswordAuthenticationToken(userDetails,null,
                            userDetails.getAuthorities());

            SecurityContext context=securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );

            return ResponseEntity.ok("session created");

        }catch (Exception e){
            return  ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("invalid token");
        }

    }
}
