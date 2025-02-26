package com.norpactech.pareto.entity;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public abstract class BaseEntity {

  private UUID id;
  private String createdBy;
  private Timestamp createdAt;
  private String updatedBy;
  private Timestamp updatedAt;
  private Boolean isActive;

  public BaseEntity() {}
  
  public BaseEntity(
      Object id, 
      Timestamp createdAt, 
      String createdBy,
      Timestamp updatedAt,
      String updatedBy,
      Boolean isActive) {
    
    this.id = (UUID) id;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
    this.isActive = isActive;
  }
}
