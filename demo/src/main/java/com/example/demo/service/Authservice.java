package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class Authservice {

    @Autowired
    AuthenticationManager authmanager;

    @Autowired
    JwtService jwtservice;

    public String verify(User user) {

        Authentication authentication=
                authmanager.authenticate
                        (new UsernamePasswordAuthenticationToken(user.getName(),user.getPassword()));

        if( authentication.isAuthenticated()){
            return jwtservice.generateToken(user.getName());
        }

        return "failure";
    }

}
