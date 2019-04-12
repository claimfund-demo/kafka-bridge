package com.redhat.summit2019;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataConverter implements TypeConverters {

    private static Logger LOG = LoggerFactory.getLogger(DataConverter.class);

    @Converter
    public Point toPoint(LoanUpdate loan) {
        Point point = Point.measurement("loan")
                .addField("loanStatus", loan.getLoanStatus())
                .tag("farmCity", loan.getFarmCity())
                .tag("farmCouncil", loan.getFarmCouncil())
                .build();

        LOG.debug("Converting loan update " + loan + " to InfluxDB point " + point.lineProtocol());

        return point;
    }
}
