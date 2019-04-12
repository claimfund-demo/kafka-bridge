package com.redhat.summit2019;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeMain extends MainListenerSupport {

    private static final String KAFKA_HOST = "127.0.0.1";
    private static final int KAFKA_PORT = 9092;
    private static final String INFLUXDB_HOST = "127.0.0.1";
    private static final int INFLUXDB_PORT = 8086;

    private static ObjectMapper jsonMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static Logger LOG = LoggerFactory.getLogger(DataConverter.class);


    public static void main(String[] args) throws Exception {
        BridgeMain main = new BridgeMain();
        main.start();
    }

    private void start() {
        var loanUpdateRoute = new RouteBuilder() {
            @Override
            public void configure() {
                final String URL = "kafka:loan?brokers=" + KAFKA_HOST + ":" + KAFKA_PORT + "&consumersCount=1";
                from(URL).process(exchange -> {
                    if (exchange.getIn() != null) {
                        Message message = exchange.getIn();
                        LOG.info("Consuming message from Kafka's loan topic == " + message.getBody(String.class));
                        LoanUpdate loanUpdate = jsonMapper.readValue(message.getBody(String.class), LoanUpdate.class);
                        LOG.info("Message " + message.getBody() + " converted to JSON " + loanUpdate);
                        exchange.getOut().setBody(loanUpdate);
                    }
                }).to("influxdb:influxConnectionBean?databaseName=loan_updates");
            }
        };

        var main = new org.apache.camel.main.Main();
        main.bind("influxConnectionBean", getInfluxDbBean());
        main.addMainListener(this);

        main.addRouteBuilder(loanUpdateRoute);
        main.enableHangupSupport();

        LOG.info("Starting up the Kafka bridge...");
        try {
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(main.getExitCode());
        }
    }

    private static InfluxDB getInfluxDbBean() {
        return new InfluxDBImpl("http://" + INFLUXDB_HOST + ":" + INFLUXDB_PORT, "", "", new OkHttpClient.Builder());
    }

    @Override
    public void configure(CamelContext context) {
        super.configure(context);
        context.getTypeConverterRegistry().addTypeConverters(new DataConverter());
    }

    @Override
    public void beforeStop(MainSupport main) {
        super.beforeStop(main);
        LOG.info("Shutting down the Kafka bridge...");
    }
}
