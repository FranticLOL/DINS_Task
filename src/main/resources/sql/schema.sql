CREATE SCHEMA IF NOT EXISTS traffic_limits;

CREATE TABLE IF NOT EXISTS traffic_limits.limits_per_hour
(
    id SERIAL PRIMARY KEY,
    limit_name VARCHAR(45) NOT NULL,
    limit_value BIGINT NOT NULL,
    effective_date TIMESTAMP NOT NULL
);

