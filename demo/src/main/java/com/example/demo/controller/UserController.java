package com.example.demo.controller;


import com.example.demo.model.User;
import com.example.demo.service.Authservice;
import com.example.demo.service.MyUserservice;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public UserController(MyUserservice service, Authservice authservice) {
        this.service = service;
        this.authservice = authservice;
    }

    private final MyUserservice service;
    private final Authservice authservice;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(10);

    @PostMapping("/register")
    public User register(@RequestBody  User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return service.register(user);
    }

    @PostMapping("/login")
    public String verifyuser(@RequestBody User user){
        return authservice.verify(user);
    }
}
