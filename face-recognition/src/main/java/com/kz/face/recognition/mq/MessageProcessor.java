package com.kz.face.recognition.mq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.pojo.Event;
import com.kz.face.recognition.constants.EventType;
import com.kz.face.recognition.service.ResultService;
import com.kz.face.recognition.service.impl.ResultServiceImpl;
import com.kz.face.recognition.utils.KryoSerializer;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

/**
 * 消息处理器
 * 
 * @author huanghaiyang 2016年1月20日
 */
public class MessageProcessor extends Thread {
  private KafkaStream<byte[], byte[]> stream;
  private ResultService resultService = new ResultServiceImpl();
  private ConsumerConnector consumer;
  private final Log logger = LogFactory.getLog(getClass());

  public MessageProcessor(KafkaStream<byte[], byte[]> stream, ConsumerConnector consumer) {
    this.stream = stream;
    this.consumer = consumer;
  }

  @Override
  public void run() {
    for (MessageAndMetadata<byte[], byte[]> msgAndMetadata : stream) {
      try {
        byte[] faceBuffer = msgAndMetadata.message();
        // 处理消息
        processe(faceBuffer);
        // 提交偏移，避免重复消费
        consumer.commitOffsets();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void processe(byte[] faceBuffer) {
    try {
      logger.info("11111111111");
      Event event = KryoSerializer.deserialization(faceBuffer, Event.class);
      if (event == null || event.getType() == 0) {
        logger.info("invaliad");
        return;
      } else {
        logger.info(event.getSession_id());
        resultService.outputResult(event.getSession_id(), "{test:333}");
        EventProcessor eventStrategy = null;
        switch (event.getType()) {
          case EventType.DETECT:
            eventStrategy=new DetectEventProcessor();
            break;
          case EventType.SEARCH:
            eventStrategy = new SearchEventProcessor();
            break;
          case EventType.ADD:
            eventStrategy = new AddEventStrategy();
            break;
          case EventType.DELETE:
            eventStrategy = new DeleteEventProcessor();
            break;
          case EventType.COMPARE:
            eventStrategy= new CompareEventProcessor();
            break;
          default:
            break;
        }
        //提交处理
        if(eventStrategy != null ){
          eventStrategy.handEvent(event);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
