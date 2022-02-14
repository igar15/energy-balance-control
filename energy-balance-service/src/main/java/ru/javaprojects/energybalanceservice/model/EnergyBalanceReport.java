package ru.javaprojects.energybalanceservice.model;

import java.time.LocalDate;
import java.util.Objects;

public class EnergyBalanceReport {
    private LocalDate date;
    private Integer mealCalories;
    private Integer trainingCalories;
    private Integer bxCalories;
    private Integer energyBalanceValue;
    private String message;

    public EnergyBalanceReport() {
    }

    public EnergyBalanceReport(LocalDate date, Integer mealCalories, Integer trainingCalories, Integer bxCalories, Integer energyBalanceValue, String message) {
        this.date = date;
        this.mealCalories = mealCalories;
        this.trainingCalories = trainingCalories;
        this.bxCalories = bxCalories;
        this.energyBalanceValue = energyBalanceValue;
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getMealCalories() {
        return mealCalories;
    }

    public void setMealCalories(Integer mealCalories) {
        this.mealCalories = mealCalories;
    }

    public Integer getTrainingCalories() {
        return trainingCalories;
    }

    public void setTrainingCalories(Integer trainingCalories) {
        this.trainingCalories = trainingCalories;
    }

    public Integer getBxCalories() {
        return bxCalories;
    }

    public void setBxCalories(Integer bxCalories) {
        this.bxCalories = bxCalories;
    }

    public Integer getEnergyBalanceValue() {
        return energyBalanceValue;
    }

    public void setEnergyBalanceValue(Integer energyBalanceValue) {
        this.energyBalanceValue = energyBalanceValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyBalanceReport that = (EnergyBalanceReport) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(mealCalories, that.mealCalories) &&
                Objects.equals(trainingCalories, that.trainingCalories) &&
                Objects.equals(bxCalories, that.bxCalories) &&
                Objects.equals(energyBalanceValue, that.energyBalanceValue) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, mealCalories, trainingCalories, bxCalories, energyBalanceValue, message);
    }

    @Override
    public String toString() {
        return "EnergyBalanceReport{" +
                "date=" + date +
                ", mealCalories=" + mealCalories +
                ", trainingCalories=" + trainingCalories +
                ", bxCalories=" + bxCalories +
                ", energyBalanceValue=" + energyBalanceValue +
                ", message='" + message + '\'' +
                '}';
    }
}