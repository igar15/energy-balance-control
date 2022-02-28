package ru.javaprojects.userservice.messaging;

import java.time.LocalDate;

public class DateMessage {
    private long userId;
    private LocalDate date;
    private boolean userBxDetailsChanged;

    public DateMessage() {
    }

    public DateMessage(long userId, LocalDate date, boolean userBxDetailsChanged) {
        this.userId = userId;
        this.date = date;
        this.userBxDetailsChanged = userBxDetailsChanged;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isUserBxDetailsChanged() {
        return userBxDetailsChanged;
    }

    public void setUserBxDetailsChanged(boolean userBxDetailsChanged) {
        this.userBxDetailsChanged = userBxDetailsChanged;
    }

    @Override
    public String toString() {
        return "DateMessage{" +
                "userId=" + userId +
                ", date=" + date +
                ", userBxDetailsChanged=" + userBxDetailsChanged +
                '}';
    }
}