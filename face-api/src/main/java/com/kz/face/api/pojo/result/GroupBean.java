package com.kz.face.api.pojo.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * rest返回的底库result消息
 * @author liuxing
 *
 */
@XmlRootElement
public class GroupBean implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String id = "";
  private String name = "";
  private boolean syncing = true;
  private boolean slave = false;
  private int version = 1;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public boolean isSyncing() {
    return syncing;
  }
  public void setSyncing(boolean syncing) {
    this.syncing = syncing;
  }
  public boolean isSlave() {
    return slave;
  }
  public void setSlave(boolean slave) {
    this.slave = slave;
  }
  public int getVersion() {
    return version;
  }
  public void setVersion(int version) {
    this.version = version;
  }
}
