package com.ltj.blog.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public final static String ES_QUEUE = "es_queue";
    public final static String ES_EXCHANGE = "es_exchange";
    public final static String ES_BINDING_KEY = "es_binding_key";

    @Bean
    public Queue exQueue() {
        return new Queue(ES_QUEUE);
    }

    @Bean
    public DirectExchange esExchange() {
        return new DirectExchange(ES_EXCHANGE);
    }

    @Bean
    public Binding esBinding(Queue exQueue, DirectExchange esExchange) {
        return BindingBuilder.bind(exQueue).to(esExchange).with(ES_BINDING_KEY);
    }
}
