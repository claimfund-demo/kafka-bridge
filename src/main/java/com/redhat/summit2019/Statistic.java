package com.redhat.summit2019;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public class Statistic {
    private String message;
    private int id;
    private String topic;

    public Statistic() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "message='" + message + '\'' +
                ", id=" + id +
                ", topic='" + topic + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return getId() == statistic.getId() &&
                getMessage().equals(statistic.getMessage()) &&
                Objects.equals(getTopic(), statistic.getTopic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getId(), getTopic());
    }
}
