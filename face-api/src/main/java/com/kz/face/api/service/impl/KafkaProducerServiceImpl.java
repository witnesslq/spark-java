package com.kz.face.api.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.api.constant.Constant;
import com.kz.face.api.pojo.result.common.Face;
import com.kz.face.api.service.KafkaProducerService;
import com.kz.face.api.utils.PropertiesUtil;
import com.kz.face.pojo.DelFaceParams;
import com.kz.face.pojo.Event;
import com.kz.face.pojo.SearchParams;

import kafka.admin.TopicCommand;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * kafka生产消息实现类
 * 
 * @author liuxing
 *
 */
public class KafkaProducerServiceImpl implements KafkaProducerService {
  private static final Log logger = LogFactory.getLog(KafkaProducerServiceImpl.class);
  // 主题
  private String topic = "";
  // 配置
  private Properties props;
  // 发送者
  private Producer<String, Event> producer;

  /**
   * 构造函数
   * 
   * @param topic kafka中存在的主题
   */
  public KafkaProducerServiceImpl() {
    logger.info(new Date()+" new KafkaProducerServiceImpl()");
    props = new Properties();
    props.put("metadata.broker.list", PropertiesUtil.getValue(Constant.KAFKABROKERS));
    props.put("key.serializer.class", PropertiesUtil.getValue(Constant.KAFKAKEYSERIALIZER));
    props.put("serializer.class", PropertiesUtil.getValue(Constant.KAFKASERIALIZER));
    props.put("partitioner.class", PropertiesUtil.getValue(Constant.KAFKAPARTITION));
    props.put("request.required.acks", PropertiesUtil.getValue(Constant.KAFKAACKS));
    producer = new Producer<String, Event>(new ProducerConfig(props));
    String topic = PropertiesUtil.getValue(Constant.KAFKATOPIC);
    this.topic = topic;
  }

  @Override
  public void produceMessage(Event event) throws Exception{
    logger.info(new Date()+" produceMessage "+event);
    try {
      producer.send(new KeyedMessage<String, Event>(topic, event.getType() + "", event));
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public void operatorTopic(String[] options) {
    logger.info(new Date()+" operatorTopic "+options);
    TopicCommand.main(options);
  }

  public static void main(String[] args) {
    KafkaProducerService service = new KafkaProducerServiceImpl();

    // 创建主题
    // String[] options = new String[]{
    // "--create",
    // "--zookeeper",
    // "192.168.10.161:2181,192.168.10.162:2181,192.168.10.163:2181",
    // "--partitions",
    // "3",
    // "--topic",
    // "face_test",
    // "--replication-factor",
    // "3",
    // };
    
    // 查看主题
    // String[] options = new String[]{
    // "--list",
    // "--zookeeper",
    // "192.168.10.161:2181,192.168.10.162:2181,192.168.10.163:2181"
    // };
    // kafkaProducerService.operatorTopic(options);

    try {
      //00000061d08-85e1-414a-81e9-6e5a14b2f626
      //000002ad24c-3b1b-4a43-963a-80c040517305
      //000002b72fe-0edf-44d5-959f-e115328f7113
//      DelFaceParams param = new DelFaceParams();
//      param.setPhoto("000002b72fe-0edf-44d5-959f-e115328f7113");
//      Event evevt = new Event();
//      evevt.setSession_id("q3q3452345");
//      evevt.setType(4);
//      evevt.setParams(param);
//      service.produceMessage(evevt);

      
      File file = new File("D:\\1.jpg");
      byte[] buffer = new byte[(int) file.length()];
      InputStream is = new FileInputStream(file);
      is.read(buffer);
      
      SearchParams param = new SearchParams();
      param.setImage(buffer);
      
      Event face = new Event();
      face.setSession_id("~~~~~~~~~~~~ddddd~~");
      face.setType(2);
      face.setParams(param);
      service.produceMessage(face);

      is.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
