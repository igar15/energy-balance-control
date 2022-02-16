package ru.javaprojects.userservice.to;

import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;
import ru.javaprojects.userservice.model.Role;
import ru.javaprojects.userservice.model.User.Sex;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class UserTo {
    private Long id;

    @NotBlank
    @Size(min = 4, max = 70)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Sex sex;

    @NotNull
    @Range(min = 10, max = 1000)
    private Integer weight;

    @NotNull
    @Range(min = 30, max = 250)
    private Integer growth;

    @NotNull
    @Range(min = 1, max = 120)
    private Integer age;

    @NotEmpty
    private Set<Role> roles;

    public UserTo() {
    }

    public UserTo(Long id, String name, Sex sex, Integer weight, Integer growth, Integer age, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.weight = weight;
        this.growth = growth;
        this.age = age;
        this.roles = roles;
    }
    public long id() {
        Assert.notNull(id, "UserTo must have id");
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserTo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", weight=" + weight +
                ", growth=" + growth +
                ", age=" + age +
                ", roles=" + roles +
                '}';
    }
}