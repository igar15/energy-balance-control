package ru.javaprojects.userservice.to;

import ru.javaprojects.userservice.model.User.Sex;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewUserTo extends BaseUserTo {

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 5, max = 32)
    private String password;

    public NewUserTo() {
    }

    public NewUserTo(String name, String email, Sex sex, Integer weight, Integer growth, Integer age, String password) {
        super(name, sex, weight, growth, age);
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "NewUserTo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                ", weight=" + weight +
                ", growth=" + growth +
                ", age=" + age +
                '}';
    }
}