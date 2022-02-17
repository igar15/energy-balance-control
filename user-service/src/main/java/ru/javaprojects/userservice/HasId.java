package ru.javaprojects.userservice;

import org.springframework.util.Assert;

public interface HasId {
    Long getId();

    void setId(Long id);

    default long id() {
        Assert.notNull(getId(), "Entity must has id");
        return getId();
    }
}