package ru.javaprojects.userservice.to;

import org.hibernate.validator.constraints.Range;
import ru.javaprojects.userservice.model.User.Sex;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public abstract class BaseUserTo {

    @NotBlank
    @Size(min = 4, max = 70)
    protected String name;

    @NotNull
    protected Sex sex;

    @NotNull
    @Range(min = 10, max = 1000)
    protected Integer weight;

    @NotNull
    @Range(min = 30, max = 250)
    protected Integer growth;

    @NotNull
    @Range(min = 1, max = 120)
    protected Integer age;

    public BaseUserTo() {
    }

    public BaseUserTo(String name, Sex sex, Integer weight, Integer growth, Integer age) {
        this.name = name;
        this.sex = sex;
        this.weight = weight;
        this.growth = growth;
        this.age = age;
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

    @Override
    public String toString() {
        return "BaseUserTo{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", weight=" + weight +
                ", growth=" + growth +
                ", age=" + age +
                '}';
    }
}