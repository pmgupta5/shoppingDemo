package com.lti.securityservice.service;

import com.lti.securityservice.dto.UserCredential;
import com.lti.securityservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    public List<UserCredential> userCredentialList= new ArrayList<>();

    public Optional<UserCredential> findByName(String username){
       return userCredentialList
               .stream()
               .filter(u->u.getUsername().equals(username))
               .findFirst();

    }
    public String saveUser(UserCredential credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        userCredentialList.add(credential);
        return "user added to the system";
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    public void validateToken(String token) {
        jwtUtil.validateToken(token);
    }

}
