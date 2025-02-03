package com.be.stack.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

  @JsonProperty("_id")
  private ObjectIdWrapper id;

  private long userId;

  @JsonProperty("info")
  private UserInfoDto info;

  public ObjectIdWrapper getId() {
    return id;
  }

  public void setId(ObjectIdWrapper id) {
    this.id = id;
  }

  public UserInfoDto getInfo() {
    return info;
  }

  public void setInfo(UserInfoDto info) {
    this.info = info;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


}
