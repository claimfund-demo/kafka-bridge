package com.redhat.summit2019;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaConsumer {

    @Incoming("test")
    @Outgoing("fromKafka")
    public String process(String message) {
        System.out.println("Message " + message + " received from Kafka");
        return message;
    }
}