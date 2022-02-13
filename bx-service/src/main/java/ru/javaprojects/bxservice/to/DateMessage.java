package ru.javaprojects.bxservice.to;

import java.time.LocalDate;

public class DateMessage {
    private long userId;
    private LocalDate date;
    private boolean userDetailsChanged;

    public DateMessage() {
    }

    public DateMessage(long userId, LocalDate date, boolean userDetailsChanged) {
        this.userId = userId;
        this.date = date;
        this.userDetailsChanged = userDetailsChanged;
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

    public boolean isUserDetailsChanged() {
        return userDetailsChanged;
    }

    public void setUserDetailsChanged(boolean userDetailsChanged) {
        this.userDetailsChanged = userDetailsChanged;
    }

    @Override
    public String toString() {
        return "BxMessage{" +
                "userId=" + userId +
                ", date=" + date +
                ", userDetailsChanged=" + userDetailsChanged +
                '}';
    }
}