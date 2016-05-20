package com.kz.face.api.service.impl;

import com.kz.face.api.utils.KryoSerializerUtils;
import com.kz.face.pojo.Event;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

/**
 * kafka序列化对象 往kafka中发送自定义对象，需要将自定义对象序列化
 * 
 * @author liuxing
 *
 */
public class EventEncoder implements Encoder<Event> {
  public EventEncoder() {}

  public EventEncoder(VerifiableProperties properties) {}

  @Override
  public byte[] toBytes(Event face) {
    return KryoSerializerUtils.serialization(face);
  }
}
