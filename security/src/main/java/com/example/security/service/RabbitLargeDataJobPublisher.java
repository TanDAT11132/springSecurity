package com.example.security.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.security.dto.BookBulkJobRequest;
import com.example.security.config.properties.RabbitPipelineProperties;

@Service
public class RabbitLargeDataJobPublisher implements LargeDataJobPublisher {

	private final RabbitTemplate rabbitTemplate;
	private final RabbitPipelineProperties rabbitPipelineProperties;

	public RabbitLargeDataJobPublisher(RabbitTemplate rabbitTemplate, RabbitPipelineProperties rabbitPipelineProperties) {
		this.rabbitTemplate = rabbitTemplate;
		this.rabbitPipelineProperties = rabbitPipelineProperties;
	}

	@Override
	public void publish(BookBulkJobRequest request) {
		try {
			rabbitTemplate.convertAndSend(
					rabbitPipelineProperties.getExchange(),
					rabbitPipelineProperties.getRoutingKey(),
					request);
		} catch (AmqpException exception) {
			throw new IllegalStateException("Khong the day job xu ly du lieu lon len RabbitMQ", exception);
		}
	}
}
