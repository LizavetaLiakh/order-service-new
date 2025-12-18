package com.innowise.order.kafka;

import com.innowise.order.dto.OrderEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final String TOPIC = "create_order_v2";

    private final KafkaTemplate<String, OrderEventDto> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends an order creation event to Kafka.
     *
     * @param orderEvent Order event DTO
     */
    public void sendCreateOrderEvent(OrderEventDto orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
        System.out.println("Sent CREATE_ORDER event: " + orderEvent);
    }
}