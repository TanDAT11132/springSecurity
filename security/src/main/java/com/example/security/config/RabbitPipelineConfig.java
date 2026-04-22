package com.example.security.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.security.config.properties.RabbitPipelineProperties;

@Configuration
public class RabbitPipelineConfig {

	@Bean
	public DirectExchange largeDataExchange(RabbitPipelineProperties properties) {
		return new DirectExchange(properties.getExchange(), true, false);
	}

	@Bean
	public Queue largeDataQueue(RabbitPipelineProperties properties) {
		return new Queue(properties.getQueue(), true);
	}

	@Bean
	public Binding largeDataBinding(
			Queue largeDataQueue,
			DirectExchange largeDataExchange,
			RabbitPipelineProperties properties) {
		return BindingBuilder.bind(largeDataQueue).to(largeDataExchange).with(properties.getRoutingKey());
	}

	@Bean
	public MessageConverter rabbitMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
