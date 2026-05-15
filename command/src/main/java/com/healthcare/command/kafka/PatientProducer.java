package com.healthcare.command.kafka;

import com.healthcare.model.PatientEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientProducer {

    private final KafkaTemplate<String, PatientEvent> kafkaTemplate;

    public void  sendEvent(PatientEvent event){
        kafkaTemplate.send("patient-topic",event);
    }
}
