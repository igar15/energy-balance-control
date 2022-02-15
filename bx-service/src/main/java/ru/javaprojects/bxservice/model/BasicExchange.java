package ru.javaprojects.bxservice.model;

import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "basic_exchanges", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"}, name = "basic_exchanges_unique_user_date_idx")})
@Access(AccessType.FIELD)
public class BasicExchange {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    private Long id;

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "calories", nullable = false)
    @NotNull
    @Range(min = 1, max = 5000)
    private Integer calories;

    @Column(name = "user_id", nullable = false)
    @NotNull
    private Long userId;

    public BasicExchange() {
    }

    public BasicExchange(Long id, LocalDate date, Integer calories) {
        this.id = id;
        this.date = date;
        this.calories = calories;
    }

    public BasicExchange(Long id, LocalDate date, Integer calories, Long userId) {
        this.id = id;
        this.date = date;
        this.calories = calories;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "BasicExchange{" +
                "id=" + id +
                ", date=" + date +
                ", calories=" + calories +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }
        BasicExchange that = (BasicExchange) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}