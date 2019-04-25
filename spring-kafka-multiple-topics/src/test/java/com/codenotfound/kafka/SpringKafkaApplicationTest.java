package com.codenotfound.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.codenotfound.kafka.consumer.Receiver;
import com.codenotfound.kafka.producer.Sender;
import com.codenotfound.model.Bar;
import com.codenotfound.model.Foo;
import com.codenotfound.model.RoomLight;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringKafkaApplicationTest {

  private final static String BAR_TOPIC = "bar.t";
  private final static String FOO_TOPIC = "foo.t";
  private final static String ROOM_TOPIC = "room.t";

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @ClassRule
  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, BAR_TOPIC, FOO_TOPIC);
           

  @Before
  public void setUp() throws Exception {
    // wait until the partitions are assigned
    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
        .getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafka.getPartitionsPerTopic());
    }
  }

  @Test
  public void testReceive() throws Exception {
   // sender.send(BAR_TOPIC, new Bar("bar"));
   // sender.send(FOO_TOPIC, new Foo("foo"));
    sender.send(FOO_TOPIC, new RoomLight("kitchen","ON"));
     sender.send(FOO_TOPIC, new RoomLight("Bathroom1","ON"));
      sender.send(FOO_TOPIC, new RoomLight("Hall2","ON"));
       sender.send(FOO_TOPIC, new RoomLight("MainDoor","ON"));
        sender.send(FOO_TOPIC, new RoomLight("BackDoor","ON"));
 
    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    
  }
}
