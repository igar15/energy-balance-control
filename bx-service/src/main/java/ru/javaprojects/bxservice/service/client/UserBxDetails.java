package ru.javaprojects.bxservice.service.client;

public class UserBxDetails {
    private Sex sex;
    private int weight;
    private int growth;
    private int age;

    public UserBxDetails() {
    }

    public UserBxDetails(Sex sex, int weight, int growth, int age) {
        this.sex = sex;
        this.weight = weight;
        this.growth = growth;
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserBxDetails{" +
                "sex=" + sex +
                ", weight=" + weight +
                ", growth=" + growth +
                ", age=" + age +
                '}';
    }

    public enum Sex {
        MAN, WOMAN;
    }
}