package com.redhat.summit2019;

import okhttp3.OkHttpClient;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;

import java.util.concurrent.atomic.AtomicInteger;

public class InfluxDbMain {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    public static void main(String[] args) throws Exception {
        final String URL = "kafka:aberdeen,fife,inverclyde?brokers=" + HOST + ":" + PORT + "&autoOffsetReset=earliest&consumersCount=1";

        var routeBuilder = new RouteBuilder() {
            @Override
            public void configure() {
                var statistic = new Statistic();
                AtomicInteger i = new AtomicInteger(0);
                from(URL).process(exchange -> {
                    if (exchange.getIn() != null) {
                        Message message = exchange.getIn();
                        String topicName = (String) message
                                .getHeader(KafkaConstants.TOPIC);
                        Object data = message.getBody();

                        statistic.setId(i.addAndGet(1));
                        statistic.setTopic(topicName);
                        statistic.setMessage(String.valueOf(data));

                        System.out.println("topicName :: "
                                + statistic.getTopic() + ", message_count/id :: "
                                + statistic.getId() + ", message :: "
                                + statistic.getMessage() + "\n");

                    }
                    exchange.getOut().setBody(statistic);
                })
            .to("influxdb:influxConnectionBean?databaseName=messages");
            }
        };

        var context = new DefaultCamelContext();

        var registryBean = new SimpleRegistry();
        registryBean.put("influxConnectionBean", getInfluxDbBean());

        context.getTypeConverterRegistry().addTypeConverters(new DataConverter());
        context.setRegistry(registryBean);
        context.addRoutes(routeBuilder);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down...");
                context.shutdown();
            } catch (Exception e) {
                System.exit(-1);
            }
        }));

        System.out.println("Starting up...");
        while(true) {
            context.start();
        }
    }

    private static InfluxDB getInfluxDbBean() {
        return new InfluxDBImpl("http://127.0.0.1:8086", "", "", new OkHttpClient.Builder());
    }
}
