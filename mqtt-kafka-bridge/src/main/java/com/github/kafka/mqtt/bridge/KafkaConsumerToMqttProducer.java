package com.github.kafka.mqtt.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * MQTT Kafka Bridge
 * 
 * @author shazin
 *
 */
public class KafkaConsumerToMqttProducer {

	private static final String MQTT_BROKER_TOPICS = "mqttbrokertopics";
	private static final String MQTT_BROKER_PORT = "mqttbrokerport";
	private static final String MQTT_BROKER_HOST = "mqttbrokerhost";
	private static final String SERIALIZER_CLASS = "serializerclass";
	private static final String BROKER_LIST = "brokerlist";

        public Properties getConsumerProps() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "simple-consumer-example");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "3000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return properties;

    }
         
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		
		options.addOption(BROKER_LIST, true, "Kafka Brokers List");
		options.addOption(SERIALIZER_CLASS, true, "Kafka Serializer Class");
		options.addOption(MQTT_BROKER_HOST, true, "MQTT Broker Host");
		options.addOption(MQTT_BROKER_PORT, true, "MQTT Broker Port");
		options.addOption(MQTT_BROKER_TOPICS, true, "MQTT Broker Topics");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
                
                           
                // Begin MQTT
		MQTT mqtt = new MQTT();
		mqtt.setHost(cmd.getOptionValue(MQTT_BROKER_HOST, "localhost"), Integer.parseInt(cmd.getOptionValue(MQTT_BROKER_PORT, "1883")));

		BlockingConnection connection = mqtt.blockingConnection();
		connection.connect();
		
		String topicsArg = cmd.getOptionValue(MQTT_BROKER_TOPICS, "test");
		List<Topic> topicsList = new ArrayList<Topic>();
		String[] topics = topicsArg.split(",");
		for(String topic:topics) {
			topicsList.add(new Topic(topic, QoS.AT_LEAST_ONCE));
		}
		Topic[] mqttTopics = topicsList.toArray(new Topic[]{});
                // End MQTT
                
                
		// Kafka Properties
		Properties properties= new Properties();
                properties.put("bootstrap.servers", "localhost:9092");
                properties.put("group.id", "simple-consumer-example");
                properties.put("auto.offset.reset", "earliest");
                properties.put("enable.auto.commit", "true");
                properties.put("auto.commit.interval.ms", "3000");
                properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                Topic[] kafkaTopics = topicsList.toArray(new Topic[]{});
                
                

	        KafkaConsumer consumer = new KafkaConsumer<Integer,String >(properties);
                  while ( true ) {
                    consumer.subscribe(Collections.singletonList("test"));  
                    ConsumerRecords<String, String> records = consumer.poll(5000);
                    for (ConsumerRecord<String, String> record : records) {
                        String message = String.format("value = %s",
                                 record.value());
                        System.out.println(message); 
                         connection.publish("ack", message.getBytes(), QoS.AT_MOST_ONCE, true);                        
                    }                 

                }     		
	}

}
