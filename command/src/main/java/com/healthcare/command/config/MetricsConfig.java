package com.healthcare.command.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter patientCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("patient.operations.total")
                .description("Total number of patient operations")
                .tag("operation", "create")
                .register(meterRegistry);
    }

    @Bean
    public Counter patientUpdatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("patient.operations.total")
                .description("Total number of patient operations")
                .tag("operation", "update")
                .register(meterRegistry);
    }

    @Bean
    public Counter patientDeletedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("patient.operations.total")
                .description("Total number of patient operations")
                .tag("operation", "delete")
                .register(meterRegistry);
    }

    @Bean
    public Timer patientCreateTimer(MeterRegistry meterRegistry) {
        return Timer.builder("patient.operations.duration")
                .description("Time taken for patient operations")
                .tag("operation", "create")
                .register(meterRegistry);
    }

    @Bean
    public Timer patientUpdateTimer(MeterRegistry meterRegistry) {
        return Timer.builder("patient.operations.duration")
                .description("Time taken for patient operations")
                .tag("operation", "update")
                .register(meterRegistry);
    }

    @Bean
    public Timer patientDeleteTimer(MeterRegistry meterRegistry) {
        return Timer.builder("patient.operations.duration")
                .description("Time taken for patient operations")
                .tag("operation", "delete")
                .register(meterRegistry);
    }
}
