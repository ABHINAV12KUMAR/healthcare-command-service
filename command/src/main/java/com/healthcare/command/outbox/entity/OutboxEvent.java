package com.healthcare.command.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name ="outbox_event")
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private  String payload ; // JSON dta
    private boolean processed;
    private LocalDateTime createdAt;
}
