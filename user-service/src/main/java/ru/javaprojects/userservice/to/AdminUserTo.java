package ru.javaprojects.userservice.to;

import ru.javaprojects.userservice.HasId;
import ru.javaprojects.userservice.model.Role;
import ru.javaprojects.userservice.model.User.Sex;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

public class AdminUserTo extends BaseUserTo implements HasId {
    private Long id;

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotEmpty
    private Set<Role> roles;

    public AdminUserTo() {
    }

    public AdminUserTo(Long id, String name, String email, Sex sex, Integer weight, Integer growth, Integer age, Set<Role> roles) {
        super(name, sex, weight, growth, age);
        this.id = id;
        this.email = email;
        this.roles = roles;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "AdminUserTo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                ", weight=" + weight +
                ", growth=" + growth +
                ", age=" + age +
                ", roles=" + roles +
                '}';
    }
}