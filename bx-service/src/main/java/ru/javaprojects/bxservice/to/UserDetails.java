package ru.javaprojects.bxservice.to;

public class UserDetails {
    private Sex sex;
    private int weight;
    private int growth;
    private int age;

    public UserDetails() {
    }

    public UserDetails(Sex sex, int weight, int growth, int age) {
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
        return "UserParams{" +
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