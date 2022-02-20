package ru.javaprojects.trainingservice.to;

import ru.javaprojects.energybalancecontrolshared.util.HasId;

public abstract class BaseTo implements HasId {
    protected Long id;

    public BaseTo() {
    }

    public BaseTo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}