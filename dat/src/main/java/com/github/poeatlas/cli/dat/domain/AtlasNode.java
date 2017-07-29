package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
  @GeneratedValue
  private int id;

  @Column(nullable = false)
  private long worldAreasKey;

  @Column(nullable = false)
  private float posX;

  @Column(nullable = false)
  private float posY;

  @Transient
  private int unknown4;

  @Transient
  private int unknown5;

  @Transient
  private int unknown6;

  @OneToMany(mappedBy = "atlasNode", cascade = CascadeType.ALL)
  private List<ConnectedAtlasNode> atlasNodeKeys;

  // @OneToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "item_visual_identity_id", insertable = false, updatable = false)
  // private ItemVisualIdentity defaultItemVisualIdentityKey;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_visual_identity_id", referencedColumnName = "id", updatable = false)
  private ItemVisualIdentity defaultItemVisualIdentityKey;


  // public ItemVisualIdentity getDefaultItemVisualIdentityKey() {
  //   return defaultItemVisualIdentityKey;
  // }
  //
  // @Spec(ListDecoder.class)
  // @OneToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "shaped_item_visual_identity_id", insertable = false, updatable = false)
  // private ItemVisualIdentity defaultShapedItemVisualIdentityKey;
  //
  // @Spec(value = StringDecoder.class, skip = true)
  // @Transient
  // private String flavourText;
  //
  // @Spec(BooleanDecoder.class)
  // @Column(nullable = false)
  // private boolean sextantable;
}
