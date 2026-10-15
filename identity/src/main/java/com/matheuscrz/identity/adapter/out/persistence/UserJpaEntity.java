package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseJpaEntity {

    @Column(name = "email_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String emailCiphertext;

    @Column(name = "email_lookup_hash", nullable = false, length = 64, unique = true)
    private String emailLookupHash;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "cpf_ciphertext", columnDefinition = "TEXT")
    private String cpfCiphertext;

    @Column(name = "cpf_lookup_hash", length = 64)
    private String cpfLookupHash;

    protected UserJpaEntity() {
    }

    UserJpaEntity(
            UUID id,
            String emailCiphertext,
            String emailLookupHash,
            String passwordHash,
            String name,
            Role role,
            UserStatus status,
            String cpfCiphertext,
            String cpfLookupHash) {
        super(id);
        this.emailCiphertext = emailCiphertext;
        this.emailLookupHash = emailLookupHash;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.status = status;
        this.cpfCiphertext = cpfCiphertext;
        this.cpfLookupHash = cpfLookupHash;
    }

    String getEmailCiphertext() {
        return emailCiphertext;
    }

    String getEmailLookupHash() {
        return emailLookupHash;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    String getName() {
        return name;
    }

    Role getRole() {
        return role;
    }

    UserStatus getStatus() {
        return status;
    }

    String getCpfCiphertext() {
        return cpfCiphertext;
    }

    String getCpfLookupHash() {
        return cpfLookupHash;
    }

    void updateEmail(String ciphertext, String lookupHash) {
        this.emailCiphertext = ciphertext;
        this.emailLookupHash = lookupHash;
    }

    void updateCpf(String ciphertext, String lookupHash) {
        this.cpfCiphertext = ciphertext;
        this.cpfLookupHash = lookupHash;
    }

    void applyChanges(User user) {
        this.passwordHash = user.passwordHash();
        this.name = user.name();
        this.role = user.role();
        this.status = user.status();
    }
}