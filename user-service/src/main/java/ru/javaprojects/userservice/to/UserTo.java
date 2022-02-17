package ru.javaprojects.userservice.to;

import org.springframework.util.Assert;
import ru.javaprojects.userservice.HasId;
import ru.javaprojects.userservice.model.User.Sex;

public class UserTo extends BaseUserTo implements HasId {
    private Long id;

    public UserTo() {
    }

    public UserTo(Long id, String name, Sex sex, Integer weight, Integer growth, Integer age) {
        super(name, sex, weight, growth, age);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                '}';
    }
}