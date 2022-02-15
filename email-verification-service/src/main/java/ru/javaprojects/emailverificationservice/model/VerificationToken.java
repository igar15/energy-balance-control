package ru.javaprojects.emailverificationservice.model;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "verification_tokens", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "verification_tokens_unique_email_idx")})
@Access(AccessType.FIELD)
public class VerificationToken {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @Column(name = "token", nullable = false)
    @NotBlank
    private String token;

    @Column(name = "expiry_date", nullable = false)
    @NotNull
    private Date expiryDate;

    @Column(name = "email_verified", nullable = false, columnDefinition = "bool default false")
    private boolean emailVerified = false;

    public VerificationToken() {
    }

    public VerificationToken(Long id, String email, String token, Date expiryDate, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.expiryDate = expiryDate;
        this.emailVerified = emailVerified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                ", emailVerified=" + emailVerified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }
        VerificationToken that = (VerificationToken) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.intValue();
    }
}