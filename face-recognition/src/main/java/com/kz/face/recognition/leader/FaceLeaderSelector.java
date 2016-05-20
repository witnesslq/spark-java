package com.kz.face.recognition.leader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.state.ConnectionState;

// import com.dahuatech.hummer.api.utils.IpUtils;

// import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.io.IOException;

/**
 * leader选举，自动把当前进程加入选举队列，并完成选举； 如果当前进程被选为leader，takeLeadership函数会被调用
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class FaceLeaderSelector extends LeaderSelectorListenerAdapter implements Closeable {

  private final Log logger = LogFactory.getLog(getClass());
  private final LeaderSelector leaderSelector;
  String selectorPath;
  volatile private boolean isLeader = false;
  volatile private boolean isClosed = false;
  FaceLeaderManager leaderManager = null;

  public FaceLeaderSelector(CuratorFramework client, String path, FaceLeaderManager leaderManager) {

    this.isLeader = false;
    this.selectorPath = path;
    this.leaderManager = leaderManager;

    // create a leader selector using the given path for management
    // all participants in a given leader selection must use the same path
    // ExampleClient here is also a LeaderSelectorListener but this isn't required
    this.leaderSelector = new LeaderSelector(client, this.selectorPath, this);
    // for most cases you will want your instance to requeue when it relinquishes leadership
    this.leaderSelector.autoRequeue();
  }

  public void start() throws IOException {
    leaderSelector.start();
  }

  @Override
  public void close() throws IOException {
    isClosed = true;
    isLeader = false;
    try {
      Thread.sleep(30);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (null != leaderSelector) {
      leaderSelector.close();
    }

  }

  @Override
  public void takeLeadership(CuratorFramework client) throws Exception {
    // we are now the leader. This method should not return until we want to relinquish leadership
    if (isClosed) {
      return;
    }
    isLeader = true;
    leaderManager.AddEvent(new BecomeLeaderEvent());
    while (isLeader) {
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {

      }
    }
    logger.info("leave leadership.\n");

  }

  @Override
  public void stateChanged(CuratorFramework client, ConnectionState newState) {
    logger.info("Connection State :" + newState);
    if ((newState == ConnectionState.SUSPENDED) || (newState == ConnectionState.LOST)) {
      if (isLeader) {
        leaderManager.AddEvent(new CancelLeaderEvent());
      }
      isLeader = false;

    }
  }
}
