package com.github.poeatlas.cli.dat.domain;

import com.github.poeatlas.cli.dat.annotation.Spec;
import com.github.poeatlas.cli.dat.decoder.BooleanDecoder;
import com.github.poeatlas.cli.dat.decoder.DoubleDecoder;
import com.github.poeatlas.cli.dat.decoder.IntDecoder;
import com.github.poeatlas.cli.dat.decoder.ListDecoder;
import com.github.poeatlas.cli.dat.decoder.LongDecoder;
import com.github.poeatlas.cli.dat.decoder.ReferenceDecoder;
import com.github.poeatlas.cli.dat.decoder.StringDecoder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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

  @Spec(LongDecoder.class)
  @Column(nullable = false)
  private long worldAreasKey;

  @Spec(DoubleDecoder.class)
  @Column(nullable = false)
  private double posX;

  @Spec(DoubleDecoder.class)
  @Column(nullable = false)
  private double posY;

  @Spec(value = IntDecoder.class, skip = true)
  @Transient
  private int unknown4;

  @Spec(value = IntDecoder.class, skip = true)
  @Transient
  private int unknown5;

  @Spec(value = IntDecoder.class, skip = true)
  @Transient
  private int unknown6;

  @Spec(ListDecoder.class)
  @OneToMany(mappedBy = "atlasNode")
  private List<ConnectedAtlasNode> atlasNodeKeys;

  @Spec(ReferenceDecoder.class)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_visual_identity_id", insertable = false, updatable = false)
  private ItemVisualIdentity defaultItemVisualIdentityKey;

  @Spec(ReferenceDecoder.class)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shaped_item_visual_identity_id", insertable = false, updatable = false)
  private ItemVisualIdentity defaultShapedItemVisualIdentityKey;

  @Spec(value = StringDecoder.class, skip = true)
  @Transient
  private String flavourText;

  @Spec(BooleanDecoder.class)
  @Column(nullable = false)
  private boolean sextantable;
}
