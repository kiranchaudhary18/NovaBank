package com.novabank.backend.config;

import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.UserStatus;
import com.novabank.backend.repository.RoleRepository;
import com.novabank.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Startup class that seeds default user security roles and a system administrator user in the database.
 *
 * @author Senior Java Backend Architect
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        log.info("DatabaseSeeder: Checking user roles initialization status...");
        try {
            Arrays.stream(RoleType.values()).forEach(roleType -> {
                if (roleRepository.findByRoleName(roleType).isEmpty()) {
                    Role role = Role.builder()
                            .roleName(roleType)
                            .build();
                    roleRepository.save(role);
                    log.info("DatabaseSeeder: Seeded missing role -> {}", roleType);
                }
            });
            log.info("DatabaseSeeder: Database roles synchronization completed.");
        } catch (Exception exception) {
            log.error("DatabaseSeeder failed to initialize roles: ", exception);
        }
    }

    private void seedAdminUser() {
        log.info("DatabaseSeeder: Checking administrator account status...");
        try {
            if (!userRepository.existsByEmail("admin@novabank.com")) {
                Role adminRole = roleRepository.findByRoleName(RoleType.ROLE_ADMIN)
                        .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not initialized."));

                User admin = User.builder()
                        .fullName("System Administrator")
                        .email("admin@novabank.com")
                        .password(passwordEncoder.encode("AdminPassword123"))
                        .phone("+12025550199")
                        .role(adminRole)
                        .status(UserStatus.ACTIVE)
                        .emailVerified(true)
                        .build();

                userRepository.save(admin);
                log.info("DatabaseSeeder: Seeded default administrator account -> admin@novabank.com");
            } else {
                log.info("DatabaseSeeder: Administrator account already exists.");
            }
        } catch (Exception exception) {
            log.error("DatabaseSeeder failed to seed administrator account: ", exception);
        }
    }
}
