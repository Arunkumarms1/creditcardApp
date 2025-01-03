package com.bank.creditcard.service;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.models.UserDto;
import com.bank.creditcard.exceptionhandler.InvalidInput;
import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankUserService implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(BankUserService.class);
    private final BankUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public BankUserService(BankUserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<BankUser> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    /**
     * Adds a new user to system
     *
     * @param userDto user info
     * @return status string
     * @throws InvalidInput Exception if username exists
     */
    public String addUser(UserDto userDto) throws InvalidInput {
        Optional<BankUser> existing = userRepository.findByUsername(userDto.getUsername());
        if (existing.isEmpty()) {
            BankUser user = new BankUser();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            Role role = new Role("USER");
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            roleRepository.saveAll(roles);
            return userRepository.save(user).getUsername() + " Added";
        } else {
            log.error("Username already exists");
            throw new InvalidInput("Username exists");
        }
    }

    /**
     * Updates an existing user
     *
     * @param userDto user info
     * @return UserDto
     */
    public UserDto updateUser(UserDto userDto) throws InvalidInput {
            Optional<BankUser> existing = userRepository.findByUsername(userDto.getUsername());
            BankUser user = existing.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            user.setEmail(userDto.getEmail());
            user.setPhone(userDto.getPhone());
            Set<Role> roles = new HashSet<>();
            userDto.getRoles().forEach(role -> roles.add(new Role(role)));
            roleRepository.saveAll(roles);
            user.setRoles(roles);
            userRepository.save(user);
            return userDto;
    }


    /**
     * Returns Credit cards of user
     *
     * @param username username
     * @return Set of credit cards
     */
    public Set<CreditCardDto> getCards(String username) {
        BankUser user = userRepository.findByUsername(username).orElseThrow();
        Set<CreditCardDto> cards = new HashSet<>();
        user.getCreditCards().forEach(
                creditCard -> cards.add(new CreditCardDto(creditCard.getCardNumber(), creditCard.getAvailableLimit())));
        return cards;
    }

    /**
     * Deletes a user
     *
     * @param username username
     * @return String status
     */
    public String deleteUser(String username) {
        BankUser user = userRepository.findByUsername(username).orElseThrow();
        userRepository.delete(user);
        return String.format("User %s deleted", username);
    }

    /**
     * Returns user details
     *
     * @param username username
     * @return User details
     */
    public UserDto getUser(String username) {
        Optional<BankUser> existing = userRepository.findByUsername(username);
        BankUser user = existing.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setRoles(user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toSet()));
        return userDto;
    }
}
