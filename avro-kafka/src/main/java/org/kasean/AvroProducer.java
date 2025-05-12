package org.kasean;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kasean.v2.User;

import java.util.Date;
import java.util.Properties;

public class AvroProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, "false");
        props.put(AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION, "true");

        try (Producer<String, User> producer = new KafkaProducer<>(props)) {
            User user = User.newBuilder()
                    .setId(1)
                    .setName("Alice")
                    .setEmail("alice@example.com")
                    .build();

            ProducerRecord<String, User> record = new ProducerRecord<>("users", user);

            producer.send(record, (metadata, e) -> {
                if (e == null) {
                    System.out.printf("Sent to %s-%d [%s]%n",
                            metadata.topic(),
                            metadata.partition(),
                            new Date(metadata.timestamp()));
                } else {
                    System.err.println("Error sending message:");
                    e.printStackTrace();
                }
            });

            producer.flush();
        }
    }
}