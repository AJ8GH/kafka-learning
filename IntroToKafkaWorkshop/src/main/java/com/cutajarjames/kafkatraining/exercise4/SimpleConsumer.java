package com.cutajarjames.kafkatraining.exercise4;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

/**
 * Write a simple kafka consumer that consumes to the TOPIC below from beginning and prints out the records
 * Can you guess the sequences on each key?
 * Answers:
 * 1. Triangle
 * 2. Fibonacci
 * 3. Primes
 */

//1. Change the servers here
public class SimpleConsumer {
    private final static String TOPIC = "kafkaTrainingBasketball";
    private final static String EARLIEST = "earliest";
    private final static String AUTO_COMMIT_FALSE = "false";
    private final static String SERVERS = "ie1-kdp001-qa.qa.betfair:9092,ie1-kdp002-qa.qa.betfair:9092,ie1-kdp003-qa.qa.betfair:9092";
    private final static Duration DEFAULT_POLL_DURATION = Duration.ofMillis(100);

    public static void main(String[] args) {
        var simpleConsumer = new SimpleConsumer();
        simpleConsumer.startConsumer();
    }

    private Consumer<String, String> createConsumer() {
        var config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new KafkaConsumer<>(config);
    }

    private void startConsumer() {
        try (var consumer = createConsumer()) {
            consumer.subscribe(Collections.singleton(TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(DEFAULT_POLL_DURATION);
                records.forEach(r -> System.out.printf("Key: %s, Value: %s, Offset: %s\n",
                        r.key(), r.value(), r.offset()));
                consumer.commitAsync();
            }
        }
    }
}
