package com.kz.face.api.pojo.result;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.kz.face.api.pojo.result.common.Photos;

/**
 * rest返回的批量检索result消息
 * @author liuxing
 *
 */
@XmlRootElement
public class BatchSearchBean implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Object source;
  private List<Photos> photos;
  public Object getSource() {
    return source;
  }
  public void setSource(Object source) {
    this.source = source;
  }
  public List<Photos> getPhotos() {
    return photos;
  }
  public void setPhotos(List<Photos> photos) {
    this.photos = photos;
  }
}
