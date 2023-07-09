package com.marble.admin.security;

import com.marble.admin.user.UserRepository;
import com.marble.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MarbleUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.getUserByEmail(email);
        if (user != null) {
            return new MarbleUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with email: " + email);
    }
}
