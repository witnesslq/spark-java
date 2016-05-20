package com.kz.face.api.service;

/**
 * 消费者接口<br>
 * 接口说明:<br>
 * 1.该接口为kafka消费者接口 , 其实现类的构造方法中指定 topic和numThreads(消费者个数 , 不指定则为1)<br>
 * 2.numThreads和topic分区个数应该一致 , 如果numThreads(假设为1)小于topic分区个数 , 则所有的topic分区数据由该线程消费 <br>
 * 3.numThreads不能大于topic分区个数 , 会造成机器空跑 , 影响性能。<br>
 * 4.但通过构造方法设定的numThreads的固定的,消费者个数不能动态添加、删除<br>
 * 5.对于单机可以通过numThreads设定来发挥机器的最大性能。对于集群来说单机到达了瓶颈之后,则需要集群(动态添加消费者)<br>
 * 6.只需要在添加的集群机器上重新构造该接口实现,指定同一个topic和numThreads即可<br>
 * 7.注意:添加完后的总消费者数量(每个机器上的消费者总和)跟topic数量一样。
 * 
 * @author liuxing
 *
 */
public interface KafkaConsumerService {
  /**
   * 从kafka中获取用户数据信息
   */
  void consume();
  /**
   * 关闭消费(业务中断后,不能再消费)
   */
  void shutdown();
}
