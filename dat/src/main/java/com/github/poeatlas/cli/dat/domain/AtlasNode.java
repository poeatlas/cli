package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by blei on 7/12/17.
 */
@Entity
@Slf4j
@Data
@ToString
@Table(name = "atlas_node")
public class AtlasNode {
  @Id
  @GeneratedValue
  private int id;

  @Column(nullable = false)
  private long worldAreasKey;

  @Column(nullable = false)
  private double posX;

  @Column(nullable = false)
  private double posY;

  @Column(nullable = false)
  private int unknown4;

  @Column(nullable = false)
  private int unknown5;

  @Column(nullable = false)
  private int unknown6;

  @OneToMany(mappedBy = "atlasNode")
  private List<ConnectedAtlasNode> atlasNodeKeys;

  @Column(nullable = false)
  private long visualKey;

  @Column(nullable = false)
  private long shapedVisualKey;

  @Column(nullable = false)
  private String flavourText;

  @Column(nullable = false)
  private boolean flag;
}
