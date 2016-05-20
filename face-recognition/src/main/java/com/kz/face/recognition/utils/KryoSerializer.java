package com.kz.face.recognition.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * kryo序列化工具类
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class KryoSerializer {
  private static Kryo kryo = null;
  static {
    kryo = new Kryo();
    kryo.setReferences(false);
    kryo.setRegistrationRequired(false);
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    kryo.setClassLoader(KryoSerializer.class.getClassLoader());
  }

  /**
   * 反序列化
   * 
   * @param b 字节数据
   * @param c 返回类型
   * @return
   */
  public static <T> T deserialization(byte[] b, Class<T> c) {
    ByteArrayInputStream in = null;
    Input input = null;
    T result = null;
    try {
      in = new ByteArrayInputStream(b);
      input = new Input(in);
      result = kryo.readObject(input, c);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CloseableUtils.close(input);
      CloseableUtils.close(in);
    }
    return result;
  }

  /**
   * 序列化
   * 
   * @param obj 待序列化对象
   * @return
   */
  public static byte[] serialization(Object obj) {
    ByteArrayOutputStream out = null;
    Output output = null;
    try {
      out = new ByteArrayOutputStream();
      output = new Output(out);
      kryo.writeObject(output, obj);
      output.flush();
      return out.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CloseableUtils.close(output);
      CloseableUtils.close(out);
    }
    return new byte[] {};
  }
}
