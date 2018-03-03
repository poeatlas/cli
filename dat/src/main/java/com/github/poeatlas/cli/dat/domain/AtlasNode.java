package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by blei on 7/12/17.
 * dependent on ItemVisualIdentity dat file
 */
@Entity
@Slf4j
@Data
@ToString
@Table(name = "atlas_node")
public class AtlasNode {

  @Id
  private int id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "world_areas_id", referencedColumnName = "id", updatable = false)
  // @Column(nullable = false)
  private WorldAreas worldAreas;

  @Column(nullable = false)
  private float x;

  @Column(nullable = false)
  private float y;

  @Transient
  private Integer unknown4;

  @Transient
  private Integer unknown5;

  @Transient
  private Integer unknown6;

  @OneToMany(mappedBy = "atlasNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ConnectedAtlasNode> atlasNodeKeys;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "item_visual_identity_id", referencedColumnName = "id", updatable = false)
  private ItemVisualIdentity defaultItemVisualIdentityKey;

}
