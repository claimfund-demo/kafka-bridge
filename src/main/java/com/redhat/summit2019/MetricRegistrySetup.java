package com.redhat.summit2019;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.metrics.app.CounterImpl;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class MetricRegistrySetup {
    @Inject
    MetricRegistry metricRegistry;

    public void setupMetrics(@Observes StartupEvent ev) {
        System.out.println("Setting up custom metrics...");

        Metadata counterMetadata = new Metadata(CustomMetrics.CONSUMED_MESSAGES, MetricType.COUNTER);
        counterMetadata.setDescription("Displays the number of consumed messages from Kafka");

//        Metadata budgetMetadata = new Metadata(CustomMetrics.TOTAL_BUDGET, MetricType.GAUGE);
//        budgetMetadata.setDescription("Displays the available budget");

        metricRegistry.register(counterMetadata, new CounterImpl());
//        metricRegistry.register(budgetMetadata, new MeterImpl());
    }
}
