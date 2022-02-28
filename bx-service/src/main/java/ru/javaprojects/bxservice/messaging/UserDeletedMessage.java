package ru.javaprojects.bxservice.messaging;

public class UserDeletedMessage {
    private long userId;
    private String email;

    public UserDeletedMessage() {
    }

    public UserDeletedMessage(long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserDeletedMessage{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                '}';
    }
}