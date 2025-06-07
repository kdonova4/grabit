package com.kdonova4.grabit.security;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppRoleRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.domain.EmailService;
import com.kdonova4.grabit.model.AppRole;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.AppUserDetails;
import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {
    private final AppUserRepository repository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final EmailService emailService;

    public AppUserService(AppUserRepository repository, AppRoleRepository appRoleRepository, PasswordEncoder passwordEncoder, AddressRepository addressRepository, EmailService emailService) {
        this.repository = repository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.emailService = emailService;
    }

    private final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();

    public Optional<AppUser> findUserById(int id) {
        return repository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = repository.findByUsername(username);

        if(appUser.isEmpty() || appUser.get().isDisabled()) {
            throw new UsernameNotFoundException(username + " NOT FOUND");
        }

        return new AppUserDetails(appUser.get());
    }


    public AppUser create(String email, String username, String password, List<String> roles) {
        validateUsername(username);
        validatePassword(password);
        validateEmail(email);

        password = passwordEncoder.encode(password);

        Set<AppRole> userRoles = roles.stream()
                .map(roleName -> appRoleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role Not Found: " + roleName)))
                .collect(Collectors.toSet());



        AppUser appUser = new AppUser(0, username, email, password, true, userRoles);

        int code = new Random().nextInt(900000) + 100000;

        try {
            emailService.sendConfirmationEmail(email, code);
            verificationCodes.put(email, code);
            return repository.save(appUser);
        } catch (Exception e) {
            throw new IllegalStateException("Failure to send confirmation email");
        }

    }

    public AppUser addSellerRole(AppUser user) {
        if(addressRepository.findAddressByUser(user).isEmpty()) {
            throw new ValidationException("User needs one Address associated with thier account to register as seller");
        }



        AppRole appRole = appRoleRepository.findByRoleName("SELLER").orElseThrow(() -> new ValidationException("Seller role DOES NOT EXIST"));


        Set<AppRole> newRoles = new HashSet<>(user.getRoles());
        newRoles.add(appRole);
        user.setRoles(newRoles);

        return repository.save(user);
    }

    public boolean validateCode(String email, int code) {
        Integer storedCode = verificationCodes.get(email);

        if(storedCode != null && storedCode.equals(code)) {
            Optional<AppUser> optionalAppUser = repository.findByEmail(email);

            if(optionalAppUser.isPresent()) {
                AppUser appUser = optionalAppUser.get();
                appUser.setDisabled(false);
                repository.save(appUser);
                verificationCodes.remove(email);
                return true;
            } else {
                throw new ValidationException("User CANNOT BE FOUND");
            }
        }

        return false;
    }

    public void validateUsername(String username) {

        if(username == null || username.isBlank()) {
            throw new ValidationException("Username is REQUIRED");
        }

        if(username.length() > 50) {
            throw new ValidationException("Username MUST BE LESS THAN 50 CHARACTERS");
        }
    }

    private void validatePassword(String password) {
        if(password == null || password.isBlank()) {
            throw new ValidationException("Password MUST BE AT LEAST 8 CHARACTERS");
        }

        int digits = 0;
        int letters = 0;
        int others = 0;

        for(char c : password.toCharArray()) {
            if(Character.isDigit(c)) {
                digits++;
            } else if(Character.isLetter(c)) {
                letters++;
            } else {
                others++;
            }
        }

        if(digits == 0 || letters == 0 || others == 0) {
            throw new ValidationException("Password MUST CONTAIN A DIGIT, A LETTER, AND A NON-DIGIT/NON-LETTER");
        }
    }

    private void validateEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new ValidationException("Email is REQUIRED");
        }

        if(email.length() > 254) {
            throw new ValidationException("Email MUST BE LESS THAN 255 CHARACTERS");
        }

        if(!email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Email appears to be invalid");
        }
    }

}
