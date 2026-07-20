package com.example.demo.controller;


import com.example.demo.Dto.userDto;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import com.example.demo.service.Authservice;
import com.example.demo.service.MyUserservice;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
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
    public ResponseEntity<?> verifyuser(@RequestBody userDto user,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String token=authservice.verify(user,
                                       request,
                                       response);
        return ResponseEntity.ok(token);
    }



    @PostMapping("/jwt/verify")
    public ResponseEntity<?> verifyTokenAndGenerateSession(HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           @AuthenticationPrincipal UserPrincipal currentUser){

        String authHeader=request.getHeader("Authorization");
        if(authHeader==null ||!authHeader.startsWith("Bearer ")){
            return  ResponseEntity.badRequest().body("bad request");
        }
        String token=authHeader.substring(7);
        return authservice.verifyTokenAndSession(token,
                                               request,
                                               response,
                                            currentUser);
    }
}





