package com.hooldus.controllers;

import com.hooldus.models.ApplicationUser;
import com.hooldus.repositories.ApplicationUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import static com.hooldus.constants.SecurityConstants.HEADER_NAME;
import static com.hooldus.constants.SecurityConstants.KEY;

@RestController
@RequestMapping("/users")
public class UserController {
    private final ApplicationUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(ApplicationUserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping()
    public List<ApplicationUser> getAllUsers() {
        return applicationUserRepository.findAll();
    }

    @PostMapping("/record")
    public void signUp(@RequestBody ApplicationUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);
    }

    @GetMapping("/metadata")
    public ApplicationUser metadata(HttpServletRequest request) {
        String token = request.getHeader(HEADER_NAME);

        Claims user = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(KEY.getBytes()))
                .parseClaimsJws(token)
                .getBody();

        Optional<ApplicationUser> appUser = Optional.ofNullable(applicationUserRepository.findByUsername(user.getSubject()));
        if (appUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }

        return appUser.get();
    }
}