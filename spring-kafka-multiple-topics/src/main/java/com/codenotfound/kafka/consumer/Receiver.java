package com.codenotfound.kafka.consumer;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import com.codenotfound.model.Bar;
import com.codenotfound.model.Foo;
import com.codenotfound.model.RoomLight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

public class Receiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
  private  String temperature = new String("21degree C");
  

  private CountDownLatch latch = new CountDownLatch(10);

  public CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(topics = "${kafka.topic.bar}")
  public void receiveBar(RoomLight room) {
   LOGGER.info("received {}", room.toString());    
   room.setstatus("OFF");
   
    kafkaTemplate
        .send(MessageBuilder.withPayload(room).setHeader(KafkaHeaders.TOPIC, "foo.t").build());
     LOGGER.info("sentTo Foo:: {}", room.toString());
    latch.countDown();
  }

  /*
  @KafkaListener(topics = "${kafka.topic.foo}")
  public void receiveFoo(Foo foo) {
    LOGGER.info("received {}", foo.toString());
    latch.countDown();
  }
  
  */
  
  
 @Autowired
 private KafkaTemplate<String, ?> kafkaTemplate;
   @KafkaListener(topics = "${kafka.topic.foo}")
  public void receiveRoom(RoomLight room) {
    LOGGER.info("received {}", room.toString());
    
   room.setstatus("OFF");
    kafkaTemplate
        .send(MessageBuilder.withPayload(room).setHeader(KafkaHeaders.TOPIC, "foo.t").build());
     LOGGER.info("sentTo Foo:: {}", room.toString());
    latch.countDown();
  }
   
}
