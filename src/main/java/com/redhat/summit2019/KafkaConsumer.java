package com.redhat.summit2019;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.StringReader;

@ApplicationScoped
public class KafkaConsumer {

    @Incoming("test")
    @Outgoing("fromKafka")
    public String process(String message) {
        System.out.println("Message " + message + " received from Kafka");
        return message;
    }

    @Incoming("budget")
    @Outgoing("budget-update")
    public long budgetConsumer(String message) {
        System.out.println("Received budget update: " + message);
        JsonParser jsonParser = Json.createParser(new StringReader(message));
        String key = "";
        long value = 0;

        while (jsonParser.hasNext()) {
            final JsonParser.Event event = jsonParser.next();
            switch (event) {
                case KEY_NAME:
                    key = jsonParser.getString();
                    break;
                case VALUE_NUMBER:
                    value = jsonParser.getLong();
                    break;
            }
        }

        jsonParser.close();
        return value;
    }
}