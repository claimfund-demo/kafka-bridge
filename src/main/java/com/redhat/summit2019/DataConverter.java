package com.redhat.summit2019;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.influxdb.dto.Point;

public class DataConverter implements TypeConverters {
    @Converter
    public Point toPoint(Statistic data) {
        Point point = Point.measurement("message")
                .addField("message_count", data.getId())
                .tag("topic", data.getTopic())
                .build();
        System.out.println("Converting data " + data + " to point " + point.lineProtocol());

        return point;
    }
}
