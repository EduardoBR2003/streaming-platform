package br.edu.streamingplatform.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMqConfig {

    public static final String RECOMMENDATION_EXCHANGE = "recommendation.exchange";
    public static final String RECOMMENDATION_CREATED_ROUTING_KEY = "recommendation.created";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    @Bean
    public TopicExchange recommendationExchange() {
        return new TopicExchange(RECOMMENDATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange recommendationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(recommendationExchange)
                .with(RECOMMENDATION_CREATED_ROUTING_KEY);
    }
}
