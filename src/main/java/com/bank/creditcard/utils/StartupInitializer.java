package com.bank.creditcard.utils;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.repositories.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class StartupInitializer {

    @Autowired
    private BankUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Optional<BankUser> admin = userRepository.findByUsername("admin");
        if (admin.isEmpty()) {
            preloadData();
        }
    }

    private void preloadData() {
        BankUser admin = new BankUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("adminpass"));
        Role adminRole = new Role("ADMIN");
        admin.setRoles(Collections.singleton(adminRole));
        userRepository.save(admin);

    }
}
