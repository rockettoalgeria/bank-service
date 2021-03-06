package com.example.bankservice.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JWTService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return new User("admin", "$2y$12$Bh/IFfFcUwnYEiJZrAf6SeGPD9E.F7t7qdI8L2pyuEtds8YwmjSEe",
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}