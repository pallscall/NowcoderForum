package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 私信
 */
@Data
public class Message {
   private int id;
   private int fromId;
   private int toId;
   private String conversationId;
   private String content;
   private int status;
   private Date createTime;
}
