package com.redhat.summit2019;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.summit2019.influxdb.LoanDTO;
import okhttp3.OkHttpClient;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.impl.InfluxDBImpl;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BridgeMain extends MainListenerSupport {

    private static final String KAFKA_HOST = System.getenv().getOrDefault("KAFKA_HOST", "127.0.0.1");
    private static final int KAFKA_PORT = Integer.parseInt(System.getenv().getOrDefault("KAFKA_PORT", "9092"));
    private static final String INFLUXDB_HOST = System.getenv().getOrDefault("INFLUXDB_HOST", "127.0.0.1");
    private static final int INFLUXDB_PORT = Integer.parseInt(System.getenv().getOrDefault("INFLUXDB_PORT", "8086"));
    private static final String LOAN_TOPIC = System.getenv().getOrDefault("LOAN_TOPIC", "claimfund");
    private static final String BUDGET_TOPIC = System.getenv().getOrDefault("BUDGET_TOPIC", "budget");
    private static final String LOAN_UPDATES_DB = System.getenv().getOrDefault("LOAN_UPDATES_DB", "loan_updates");
    private static final String BUDGET_UPDATES_DB = System.getenv().getOrDefault("BUDGET_UPDATES_DB", "budget_updates");

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
                final String URL = "kafka:"+ LOAN_TOPIC + "?brokers=" + KAFKA_HOST + ":" + KAFKA_PORT + "&consumersCount=1";
                from(URL).process(exchange -> {
                    if (exchange.getIn() != null) {
                        var message = exchange.getIn();
                        LOG.debug("Consuming message from Kafka's loan topic == " + message.getBody(String.class));
                        var loanUpdate = jsonMapper.readValue(message.getBody(String.class), LoanUpdate.class);
                        LOG.debug("Message " + message.getBody(String.class) + " converted to " + loanUpdate.toString());

                        checkForPendingEntry(loanUpdate);

                        exchange.getOut().setBody(loanUpdate);
                    }
                }).to("influxdb:influxConnectionBean?databaseName=" + LOAN_UPDATES_DB);
            }
        };

        var budgetUpdate = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                final String URL = "kafka:" + BUDGET_TOPIC + "?brokers=" + KAFKA_HOST + ":" + KAFKA_PORT + "&consumersCount=1";
                from(URL).process(exchange -> {
                    if (exchange.getIn() != null) {
                        var message = exchange.getIn();
                        LOG.debug("Consuming message from Kafka's budget topic ==" + message.getBody(String.class));
                        Map budgetUpdate = jsonMapper.readValue(message.getBody(String.class), Map.class);
                        LOG.debug("Message " + message.getBody(String.class) + " converted to " + budgetUpdate);
                        exchange.getOut().setBody(budgetUpdate.get("budget"));
                    }
                }).to("influxdb:influxConnectionBean?databaseName=" + BUDGET_UPDATES_DB);
            }
        };

        var main = new org.apache.camel.main.Main();
        main.bind("influxConnectionBean", getInfluxDbBean());
        main.addMainListener(this);

        main.addRouteBuilder(loanUpdateRoute);
        main.addRouteBuilder(budgetUpdate);

        LOG.info("Starting up the Kafka bridge...");
        try {
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(main.getExitCode());
        }
    }

    private void checkForPendingEntry(LoanUpdate loanUpdate) {
        if (!"pending".equalsIgnoreCase(loanUpdate.getLoanStatus())) {
            try (var db = getInfluxDbBean()) {
                LOG.debug("Loan " + loanUpdate.toString() + " has been " + loanUpdate.getLoanStatus()
                        + ". Replacing data in InfluxDB");
                var query = new Query("SELECT * from loan WHERE applicationID = " + loanUpdate.getApplicationID(), "loan_updates");
                var dbMapper = new InfluxDBResultMapper();
                var result = db.query(query);
                var rows = dbMapper.toPOJO(result, LoanDTO.class);
                if (rows == null) {
                    return;
                }
                for (LoanDTO loan : rows) {
                    if ("Pending".equalsIgnoreCase(loan.getLoanStatus()) && loan.getApplicationID() == loanUpdate.getApplicationID()) {
                        var instantInNano = ChronoUnit.NANOS.between(Instant.EPOCH, loan.getTime());
                        var point = Point.measurement("loan")
                                .time(instantInNano, TimeUnit.NANOSECONDS)
                                .addField("applicationID", loan.getApplicationID())
                                .addField("loanStatus", "updated")
                                .tag("farmCouncil", loan.getFarmCouncil())
                                .tag("farmCity", loan.getFarmCity())
                                .build();
                        LOG.debug("Invalidating pending application " + loan.getApplicationID() + " with " + point.lineProtocol());
                        db.setDatabase("loan_updates");
                        db.setRetentionPolicy("autogen");
                        db.write(point);
                    }
                }
            }
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
