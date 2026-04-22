package com.example.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.pipeline.rabbit")
public class RabbitPipelineProperties {
	private String exchange = "large-data.exchange";
	private String routingKey = "large-data.job";
	private String queue = "large-data.job.queue";
}
