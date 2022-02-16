package ru.javaprojects.trainingservice.to;

import org.springframework.util.Assert;

public abstract class BaseTo {
    protected Long id;

    public BaseTo() {
    }

    public BaseTo(Long id) {
        this.id = id;
    }

    public long id() {
        Assert.notNull(id, "To must have id");
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}