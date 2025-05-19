package vn.hoidanit.jobhunter.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import vn.hoidanit.jobhunter.service.UserService;

@Component("userDetailService")
public class UserCustomDetail implements UserDetailsService {
    private final UserService userService;

    public UserCustomDetail(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== TRYING TO LOAD USER: " + username + " ===");

        vn.hoidanit.jobhunter.domain.User user = this.userService.handleGetUserByUserName(username);
        if (user == null) {
            System.out.println("=== USER NOT FOUND IN DB ===");
            throw new UsernameNotFoundException("User not found");
        }

        System.out.println("=== USER FOUND ===");
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + user.getPassword());
        System.out.println("CreatedAt: " + user.getCreatedAt());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}