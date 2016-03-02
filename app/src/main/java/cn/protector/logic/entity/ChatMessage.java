package cn.protector.logic.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：宝贝信息实体类
 *
 * @author Created by Administrator on 2015/9/4.
 */
public class ChatMessage implements Serializable{
  private int time;
  private String image;
  private String content;
  private String sender;
  private String url;

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFormatTime() {
    return new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss").format(new Date(time));
  }
}
