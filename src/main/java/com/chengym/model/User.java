package com.chengym.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Data;

import java.util.Date;

@Data
public class User {

  private String id;
  private String name;
  private String password;
  private String phone;
  private String roles;
  private String userId;
  private String jobNumber;
  private String realName;
  private String avatarUrl;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
  private Date createdTime;
  private int source;
}
