package com.be.stack.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {

  @JsonProperty("_id")
  private ObjectIdWrapper id;

  private long userId;

  private boolean isBot;

  private String firstName;

  private String lastName;

  private String username;

  private String languageCode;

  private boolean isPremium;

  private boolean addedToAttachmentMenu;

  private boolean allowsWriteToPm;

  private String photoUrl;

  @JsonProperty("_class")
  private String className;

  // Getters and Setters
  public ObjectIdWrapper getId() {
    return id;
  }

  public void setId(ObjectIdWrapper id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public boolean isBot() {
    return isBot;
  }

  public void setBot(boolean bot) {
    isBot = bot;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  public boolean isPremium() {
    return isPremium;
  }

  public void setPremium(boolean premium) {
    isPremium = premium;
  }

  public boolean isAddedToAttachmentMenu() {
    return addedToAttachmentMenu;
  }

  public void setAddedToAttachmentMenu(boolean addedToAttachmentMenu) {
    this.addedToAttachmentMenu = addedToAttachmentMenu;
  }

  public boolean isAllowsWriteToPm() {
    return allowsWriteToPm;
  }

  public void setAllowsWriteToPm(boolean allowsWriteToPm) {
    this.allowsWriteToPm = allowsWriteToPm;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}

