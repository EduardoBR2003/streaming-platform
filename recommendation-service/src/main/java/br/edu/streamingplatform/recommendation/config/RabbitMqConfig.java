package br.edu.streamingplatform.recommendation.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;

@EnableRabbit
@Configuration
public class RabbitMqConfig {

    public static final String CONTENT_EXCHANGE = "content.exchange";
    public static final String CONTENT_VIEWED_ROUTING_KEY = "content.viewed";
    public static final String RECOMMENDATION_QUEUE = "recommendation.queue";

    public static final String RECOMMENDATION_EXCHANGE = "recommendation.exchange";
    public static final String RECOMMENDATION_CREATED_ROUTING_KEY = "recommendation.created";

    @Bean
    public TopicExchange contentExchange() {
        return new TopicExchange(CONTENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue recommendationQueue() {
        return new Queue(RECOMMENDATION_QUEUE, true);
    }

    @Bean
    public Binding recommendationBinding(Queue recommendationQueue, TopicExchange contentExchange) {
        return BindingBuilder
                .bind(recommendationQueue)
                .to(contentExchange)
                .with(CONTENT_VIEWED_ROUTING_KEY);
    }

    @Bean
    public TopicExchange recommendationExchange() {
        return new TopicExchange(RECOMMENDATION_EXCHANGE, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("br.edu.streamingplatform");
        typeMapper.setIdClassMapping(Map.of(
                "br.edu.streamingplatform.streaming.dto.ContentViewedEvent", ContentViewedEvent.class,
                ContentViewedEvent.class.getName(), ContentViewedEvent.class
        ));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
