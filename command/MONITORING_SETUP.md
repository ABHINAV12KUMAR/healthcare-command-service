# Healthcare Command Service - Monitoring Setup Guide

This document provides a comprehensive guide for setting up Prometheus and Grafana monitoring for the Healthcare Command Service following industry best practices.

## Overview

The monitoring stack includes:
- **Prometheus**: Metrics collection and storage
- **Grafana**: Visualization and dashboards
- **Alertmanager**: Alert management and notifications
- **Node Exporter**: System-level metrics (optional)

## Prerequisites

- Docker and Docker Compose installed
- Java 17
- Maven
- Healthcare Command Service application running on port 8081

## Architecture

```
┌─────────────────┐     ┌──────────────┐     ┌─────────────┐
│   Spring Boot   │────▶│  Prometheus  │────▶│  Grafana    │
│   Application   │     │  (9090)      │     │  (3000)     │
│   (8081)        │     └──────────────┘     └─────────────┘
└─────────────────┘            │                     │
                               ▼                     │
                        ┌──────────────┐             │
                        │ Alertmanager │◀────────────┘
                        │   (9093)     │
                        └──────────────┘
```

## Step-by-Step Setup

### 1. Build the Application

```bash
cd command
mvn clean package -DskipTests
```

### 2. Start the Application

```bash
java -jar target/command-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Or run with Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Verify Actuator Endpoints

```bash
# Health check
curl http://localhost:8081/actuator/health

# Prometheus metrics
curl http://localhost:8081/actuator/prometheus

# Available endpoints
curl http://localhost:8081/actuator
```

### 4. Start Monitoring Stack

```bash
cd command
docker-compose -f docker-compose.monitoring.yml up -d
```

### 5. Verify Services

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)
- **Alertmanager**: http://localhost:9093
- **Node Exporter**: http://localhost:9100/metrics

## Custom Metrics

The application includes custom business metrics for patient operations:

### Counters
- `patient_operations_total{operation="create|update|delete"}`: Total count of patient operations

### Timers
- `patient_operations_duration{operation="create|update|delete"}`: Duration of patient operations with percentiles

## Grafana Dashboard

A pre-configured dashboard is included with the following panels:

### System Health
- Application Status
- Heap Memory Usage
- CPU Usage
- DB Connection Pool

### HTTP Metrics
- Request Rate
- Response Time (Latency) - 50th and 95th percentiles
- Error Rate (4xx and 5xx)

### Business Metrics
- Patient Operations Rate (Create/Update/Delete)
- Patient Operations Duration (95th percentile)

### JVM Metrics
- JVM Heap Memory
- JVM Threads

The dashboard auto-loads when Grafana starts. Access it at:
http://localhost:3000/d/healthcare-command/healthcare-command-service-monitoring

## Alerting Rules

The following alerts are configured in `alerting_rules.yml`:

| Alert Name | Severity | Condition | Description |
|------------|----------|-----------|-------------|
| HighErrorRate | Critical | Error rate > 0.1/sec for 5min | High HTTP error rate detected |
| HighLatency | Warning | 95th percentile latency > 1s for 5min | High response latency |
| ApplicationDown | Critical | Application down for 2min | Service is unreachable |
| HighMemoryUsage | Warning | Heap memory > 90% for 5min | High memory consumption |
| HighCpuUsage | Warning | CPU usage > 80% for 5min | High CPU utilization |
| DatabaseConnectionPoolExhaustion | Critical | DB pool usage > 90% for 5min | Connection pool nearly exhausted |
| SlowPatientOperations | Warning | Patient ops 95th percentile > 2s for 5min | Slow business operations |

## Configuring Alert Notifications

Edit `alertmanager.yml` to configure email and Slack notifications:

### Email Configuration
```yaml
smtp_smarthost: 'smtp.gmail.com:587'
smtp_from: 'alerts@healthcare.com'
smtp_auth_username: 'your-email@gmail.com'
smtp_auth_password: 'your-app-password'
```

### Slack Configuration
Replace `YOUR_SLACK_WEBHOOK_URL` with your actual Slack webhook URL in the receivers section.

## Important Prometheus Queries

### Application Health
```promql
up{job="healthcare-command-service"}
```

### Error Rate
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```

### Response Time (95th percentile)
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Memory Usage
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

### Patient Operations Rate
```promql
rate(patient_operations_total[5m])
```

### Database Connection Pool
```promql
hikaricp_connections_active / hikaricp_connections_max * 100
```

## Production Considerations

### Security
1. Change default Grafana credentials
2. Enable authentication for Prometheus
3. Use HTTPS in production
4. Secure Alertmanager with authentication

### Persistence
- Data is persisted in Docker volumes:
  - `prometheus-data`: 30-day retention
  - `grafana-data`: Dashboards and settings
  - `alertmanager-data`: Alert history

### Scaling
- For high-traffic applications, consider:
  - Prometheus federation
  - Thanos for long-term storage
  - Multiple Grafana instances with load balancing

### Resource Limits
Add to `docker-compose.monitoring.yml` if needed:
```yaml
services:
  prometheus:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 4G
```

## Troubleshooting

### Prometheus not scraping metrics
- Check if application is running: `curl http://localhost:8081/actuator/prometheus`
- Verify Prometheus target status: http://localhost:9090/targets
- Check firewall rules

### Grafana dashboard not loading
- Verify datasource is connected: Configuration → Data Sources → Prometheus
- Check Grafana logs: `docker logs healthcare-grafana`

### Alerts not firing
- Check Alertmanager status: http://localhost:9093/#/status
- Verify alert rules in Prometheus: http://localhost:9090/rules
- Check alert configuration in `alertmanager.yml`

## Stopping the Monitoring Stack

```bash
docker-compose -f docker-compose.monitoring.yml down
```

To remove volumes (delete all data):
```bash
docker-compose -f docker-compose.monitoring.yml down -v
```

## Next Steps

1. Customize alert thresholds based on your SLA requirements
2. Add additional business metrics as needed
3. Set up notification channels (email, Slack, PagerDuty)
4. Configure log aggregation (ELK stack, Loki)
5. Set up distributed tracing (Jaeger, Zipkin)

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
