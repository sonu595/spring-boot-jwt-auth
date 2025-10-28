package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;
    
    public AuthResponse registerUser(Userdto userdto) {
        if (userRepository.existsByUsername(userdto.getUsername())) {
            throw new RuntimeException("User already exist: "+ userdto.getUsername());
        }

        if (userRepository.existsByEmail(userdto.getEmail())) {
            throw new RuntimeException("email alredy exist : "+ userdto.getEmail());
        }

        User user = new User();
        user.setUsername(userdto.getUsername());
        user.setPassword(passwordEncoder.encode(userdto.getPassword()));
        user.setEmail(userdto.getEmail());
        user.setFirstname(userdto.getFirstName());
        user.setLastname(userdto.getLastName());

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getUsername());

        return new AuthResponse(
            "User registered successfully",
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            token
        );
    }

    public AuthResponse loginUser(LoginDto loginDto){
        User user = userRepository.findByUsername(loginDto.getUsername())
        .orElseThrow(()-> new RuntimeException("User not found: "+ loginDto.getUsername()));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
            "Login successfully",
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            token
        );
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found with id: "+id));
    }
}