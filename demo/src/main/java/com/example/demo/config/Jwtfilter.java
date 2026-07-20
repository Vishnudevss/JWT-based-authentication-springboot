//package com.example.demo.config;
//
//import com.example.demo.service.JwtService;
//import com.example.demo.service.MyUserservice;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Configuration
//public class Jwtfilter extends OncePerRequestFilter {
//
//
//    private final JwtService jwtService;
//    private final ApplicationContext applicationContext;
//
//    public Jwtfilter(JwtService jwtService, ApplicationContext applicationContext) {
//        this.jwtService = jwtService;
//        this.applicationContext = applicationContext;
//    }
//
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader=request.getHeader("Authorization");
//        String token=null;
//        String username=null;
//
//        if(authHeader!=null && authHeader.startsWith("Bearer ")){
//            token=authHeader.substring(7);
//            username=jwtService.extractUserName(token);
//        }
//
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails= applicationContext.getBean(MyUserservice.class)
//                            .loadUserByUsername(username);
//
//            if(jwtService.validateToken(token,userDetails)){
//                UsernamePasswordAuthenticationToken authToken=
//                         new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request,response);
//
//    }
//}
