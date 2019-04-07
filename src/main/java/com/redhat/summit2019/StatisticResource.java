package com.redhat.summit2019;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
public class StatisticResource {

    private List<Statistic> messages = new ArrayList<>();
    private AtomicInteger counter = new AtomicInteger(0);
    private AtomicLong budget = new AtomicLong(0);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @GET
    @Path("test")
    @Counted(name = "number_of_messages", monotonic = true, description = "Number of received messages", absolute = true)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageCount() {
        return Response.ok(JsonbBuilder.create().toJson(messages)).build();
    }

    @Gauge(unit = "MM", name = "total_budget")
    public long getBudget() {
        return this.budget.get();
    }

    @Incoming("fromKafka")
    public void consumeMessages(String message) {
        System.out.println("Message " + message + " received from the consumer");
        var statistic = new Statistic();
        statistic.setMessage(message);
        statistic.setTopic("test");
        statistic.setId(counter.addAndGet(1));
        messages.add(statistic);
        Counter counterMetric = registry.getCounters().get(CustomMetrics.CONSUMED_MESSAGES);
        counterMetric.inc();
    }

    @Incoming("budget-update")
    public void getBudgetUpdate(long budget) {
        this.budget.set(budget);
    }
}
