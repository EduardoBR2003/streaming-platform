package br.edu.streamingplatform.streaming.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CONTENT_EXCHANGE          = "content.exchange";
    public static final String CONTENT_VIEWED_ROUTING_KEY = "content.viewed";
    public static final String RECOMMENDATION_QUEUE      = "recommendation.queue";

    @Bean
    public TopicExchange contentExchange() {
        return new TopicExchange(CONTENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue recommendationQueue() {
        return new Queue(RECOMMENDATION_QUEUE, true);
    }

    @Bean
    public Binding recommendationBinding(Queue recommendationQueue,
                                         TopicExchange contentExchange) {
        return BindingBuilder
                .bind(recommendationQueue)
                .to(contentExchange)
                .with(CONTENT_VIEWED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
