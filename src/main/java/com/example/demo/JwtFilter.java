package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    // 1. UserRepository को Autowire करें ताकि हम टोकन से यूज़र को ढूँढ सकें
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();

        // 2. पब्लिक URLs (जैसे /register और /login) के लिए फिल्टर को बाईपास करें
        if (path.equals("/api/auth/register") || path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                if (jwtService.validateToken(token)) {
                    username = jwtService.getUsernameFromToken(token); //
                }
            } catch (Exception e) {
                // टोकन इनवैलिड होने पर भी हम सीधे एरर नहीं भेजेंगे,
                // बस SecurityContext को null रहने देंगे।
                logger.warn("Invalid JWT Token: " + e.getMessage());
            }
        }
        
        // 3. (सबसे ज़रूरी) Spring Security Context को सेट करें
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // डेटाबेस से यूज़र डिटेल्स लोड करें
            User user = userRepository.findByUsername(username) //
                                      .orElse(null);
            
            if (user != null) {
                // एक ऑथेंटिकेशन ऑब्जेक्ट बनाएँ
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(user, null, null); // (यूज़र, पासवर्ड, अथॉरिटीज़)
                
                // SecurityContextHolder में ऑथेंटिकेशन सेट करें
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. अगर टोकन नहीं है या इनवैलिड है, तो भी चेन को आगे बढ़ाएँ
        // Spring Security खुद ही 401 Unauthorized एरर हैंडल कर लेगा
        // क्योंकि SecurityContext null होगा।
        filterChain.doFilter(request, response);
    }
}