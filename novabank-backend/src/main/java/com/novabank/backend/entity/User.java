package com.novabank.backend.entity;

import com.novabank.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Entity representing a user security profile in the banking platform.
 * Implements {@link UserDetails} for integration with Spring Security.
 * Extends {@link BaseEntity} to inherit UUID key and audit properties.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** Full name of the user. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Unique email address, utilized as the login username. */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /** Encrypted password string. */
    @Column(name = "password", nullable = false)
    private String password;

    /** Contact phone number. validated via custom validations on boundary. */
    @Column(name = "phone", nullable = false)
    private String phone;

    /** Associated security role authorizing capabilities. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /** Operational state of the user account. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /** Flag indicating whether the email registration has been verified. */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    // -------------------------------------------------------------------
    // UserDetails Interface Implementations
    // -------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getRoleName() == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleName().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
