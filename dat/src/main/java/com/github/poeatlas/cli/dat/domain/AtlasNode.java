package com.github.poeatlas.cli.dat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "world_areas_id", referencedColumnName = "id", updatable = false)
  // @Column(nullable = false)
  private WorldAreas worldAreas;

  @JsonProperty("x")
  @Column(nullable = false)
  private float posX;

  @JsonProperty("y")
  @Column(nullable = false)
  private float posY;

  @JsonIgnore
  @Transient
  private int unknown4;

  @JsonIgnore
  @Transient
  private int unknown5;

  @JsonIgnore
  @Transient
  private int unknown6;

  @JsonIgnore
  @OneToMany(mappedBy = "atlasNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ConnectedAtlasNode> atlasNodeKeys;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "item_visual_identity_id", referencedColumnName = "id", updatable = false)
  private ItemVisualIdentity defaultItemVisualIdentityKey;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "shaped_item_visual_identity_id", referencedColumnName = "id", updatable = false)
  private ItemVisualIdentity defaultShapedItemVisualIdentityKey;

  @JsonProperty("connected_map_ids")
  public List<Integer> getConnectedMapIds() {
    List<Integer> idList = new ArrayList<>();
    for (ConnectedAtlasNode node: getAtlasNodeKeys()) {
      idList.add(node.getId().getConnectedAtlasNodeId());
    }
    return idList;
  }

  @JsonProperty("world_areas_id")
  public Long getWorldAreasId() {
    return getWorldAreas().getId();
  }
  // @Spec(value = StringDecoder.class, skip = true)
  // @Transient
  // private String flavourText;
  //
  // @Spec(BooleanDecoder.class)
  // @Column(nullable = false)
  // private boolean sextantable;
}
