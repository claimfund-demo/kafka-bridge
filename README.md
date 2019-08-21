## Kafka -> InfluxDB bridge

### Environment variables
| Variable | Default | Description |
| --- | --- | --- |
|`KAFKA_HOST`|`127.0.0.1`|IP or hostname of the Kafka host|
|`KAFKA_PORT`|`9092`|Port number of the Kafka host|
|`INFLUXDB_HOST`|`127.0.0.1`|IP or hostname of the InfluxDB host|
|`INFLUXDB_PORT`|`8086`|Port number of the InfluxDB host|
|`LOAN_TOPIC`|`claimfund`|The Kafka topic which will receive the loan data|
|`BUDGET_TOPIC`|`budget`|The Kafka topic which will receive the budget updates|
|`LOAN_UPDATES_DB`|`loan_updates`|The InfluxDB database for the load data|
|`BUDGET_UPDATES_DB`|`budget_updates`|The InfluxDB database for the budget updates|
|`LOAN_RETENTION_POLICY`|`autogen`|The retention policy used by the loan database|
|`BUDGET_RETENTION_POLICY`|`autogen`|The retention policy used by the budget database|