package com.kz.face.recognition.leader;

import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.kz.face.recognition.utils.PropertyUtils;


/**
 * 初始化leader选择管理类，主要功能有：
 * 1.建立与zookeeper的连接（使用了curator框架，成功创建一个CuratorFramework对象，就成功的与zookeeper建立了连接） 2.启动leader选举
 * 3.处理事件，如成为leader事件和取消leader事件；
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class FaceLeaderManager extends Thread implements Closeable {

  private final Log logger = LogFactory.getLog(getClass());

  public static final String C_PATH = "/LeaderSelector";
  public static final String APP_NAME = "face-recognition";
  private static FaceLeaderManager realTimeTaskLeadManager = null;

  private CuratorFramework client = null;
  private FaceLeaderSelector realtimeSelector = null;

  volatile private boolean running = false;
  private Queue<FaceLeaderEvent> leaderEventQueue = null;
  volatile private boolean isLeader = false;


  public boolean isLeader() {
    return isLeader;
  }

  public void setLeader(boolean isLeader) {
    this.isLeader = isLeader;
  }

  private FaceLeaderManager() {

  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public boolean AddEvent(FaceLeaderEvent event) {
    if (null == leaderEventQueue) {
      return false;
    }

    return leaderEventQueue.add(event);
  }

  public String getLeaderIpandPort() throws Exception {
    if (null == client) {
      return null;
    }

    return new String(client.getData().forPath(C_PATH));
  }


  public void StartManager() throws IOException {
    logger.info("start leader manager ");
    leaderEventQueue = new ConcurrentLinkedQueue<FaceLeaderEvent>();
    String connectInfo = PropertyUtils.getSystemProperties("zookeeper.connect");
    // 与zookeeper建立连接
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    client = builder.connectString(connectInfo).sessionTimeoutMs(3000).connectionTimeoutMs(3000)
        .canBeReadOnly(false).retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
        .defaultData(null).namespace(APP_NAME).build();

    realtimeSelector = new FaceLeaderSelector(client, C_PATH, this);
    // 启动线程
    setRunning(true);
    super.start();
    client.start();
    realtimeSelector.start();

  }


  @Override
  public void close() throws IOException {
    logger.info("close leader manager");
    StopThread();

    if (null != realtimeSelector) {
      realtimeSelector.close();
    }

    if (null != client) {
      client.close();
    }

  }

  @Override
  public void run() {
    logger.info("leader manager thread is running ...");

    while (isRunning()) {
      try {
        FaceLeaderEvent currentEvent = leaderEventQueue.poll();
        if (null == currentEvent) {
          Thread.sleep(10);
        } else {
          if (currentEvent.GetEventType() == FaceLeaderEvent.EventTypeEnum.BecomeLeader) {
            setLeader(true);
            logger.info("leader manager thread: become leader ");
          } else {
            setLeader(false);
            logger.info("leader manager thread: cancel leader ");
          }
          currentEvent.Excute();
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void StopThread() {
    setRunning(false);
    while (super.isAlive()) {
      try {
        Thread.sleep(5);

      } catch (Exception e) {
      }
    }
  }

  public static FaceLeaderManager Instance() {
    if (realTimeTaskLeadManager == null) {
      realTimeTaskLeadManager = new FaceLeaderManager();
    }
    return realTimeTaskLeadManager;
  }
}
