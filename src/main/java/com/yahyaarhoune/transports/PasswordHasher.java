package com.yahyaarhoune.transports;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// You might need to add a dependency for spring-security-crypto if it's not already pulled in
// by spring-boot-starter-security for a standalone run, but usually it is.

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "12345678"; // << CHANGE THIS to the actual password you want to hash
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("Plain Password: " + plainPassword);
        System.out.println("BCrypt Hashed Password: " + hashedPassword);
    }
}