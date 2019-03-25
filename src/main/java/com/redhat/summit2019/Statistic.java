package com.redhat.summit2019;

public class Statistic {
    private String message;
    private int id;
    private String topic;

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
}
