package com.cutajarjames.kafkatraining.exercise4;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

public class SimpleProducer {
    private final static String[] INCIDENTS_TO_USE = {"HOME_POINT", "AWAY_POINT", "HOME_FOUL", "AWAY_FOUL", "NEW_PHASE", "SUBSTITUTION"};

    private final static String TOPIC = "kafkaTrainingBasketball";

    //1. Change the servers here
    private final static String SERVERS = "ie1-kdp001-qa.qa.betfair:9092,ie1-kdp002-qa.qa.betfair:9092,ie1-kdp003-qa.qa.betfair:9092";


    //2. Implement create producer
    private Producer<String, String> createProducer() {
        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //3. Send the correct serializers and return
        return new KafkaProducer<>(props);
    }

    public void startProducer() throws InterruptedException {
        try (var producer = createProducer()) {
            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                String incident = INCIDENTS_TO_USE[random.nextInt(6)];

                //5. Send the random incident, using a "<insertTeamA> vs <insertTeamB>" as key (choose whatever teams you want)
                String partitionKey = "Crystal Palace vs Arsenal";
                producer.send(new ProducerRecord<>(TOPIC, partitionKey, incident));
                Thread.sleep(3000);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var simpleProducer = new SimpleProducer();
        simpleProducer.startProducer();
    }
}
