package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, unique = true, length = 254)
    private String email;

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

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "cpf_ciphertext", columnDefinition = "TEXT")
    private String cpfCiphertext;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "cpf_lookup_hash", columnDefinition = "CHAR(64)", length = 64)
    private String cpfLookupHash;

    protected UserJpaEntity() {
    }

    UserJpaEntity(UUID id, String email, String passwordHash, String name,
                  Role role, UserStatus status, String cpfCiphertext, String cpfLookupHash) {
        super(id);
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.status = status;
        this.cpfCiphertext = cpfCiphertext;
        this.cpfLookupHash = cpfLookupHash;
    }

    String getEmail() { return email; }
    String getPasswordHash() { return passwordHash; }
    String getName() { return name; }
    Role getRole() { return role; }
    UserStatus getStatus() { return status; }
    String getCpfCiphertext() { return cpfCiphertext; }
    String getCpfLookupHash() { return cpfLookupHash; }

    void updateCpf(String ciphertext, String lookupHash) {
        this.cpfCiphertext = ciphertext;
        this.cpfLookupHash = lookupHash;
    }

    void applyChanges(User user) {
        this.name = user.name();
        this.passwordHash = user.passwordHash();
        this.role = user.role();
        this.status = user.status();
    }
}