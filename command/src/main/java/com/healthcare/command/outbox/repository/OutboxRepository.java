package com.healthcare.command.outbox.repository;

import com.healthcare.command.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findByProcessedFalse();
}
// We can implementation in pagination