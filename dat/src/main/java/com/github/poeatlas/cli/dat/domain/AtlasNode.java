package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by blei on 7/12/17.
 */
@Entity
@Slf4j
@Data
@ToString
public class AtlasNode {
  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false)
  private long worldAreasKey;

  @Column(nullable = false)
  private double posX;

  @Column(nullable = false)
  private double posY;

  @Column(nullable = false)
  private String visualKey;

  @Column(nullable = false)
  private String shapedVisualKey;

  @Column(nullable = false)
  private boolean flag;
}
