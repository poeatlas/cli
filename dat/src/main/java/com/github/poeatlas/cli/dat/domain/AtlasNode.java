package com.github.poeatlas.cli.dat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
  // private static final int TIER_MAGIC_NUMBER = 67;

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
  @JoinColumn(name = "shaped_item_visual_identity_id",
      referencedColumnName = "id",
      updatable = false)
  private ItemVisualIdentity defaultShapedItemVisualIdentityKey;

  /**
   * get connected atlas node ids for this atlas node.
   * @return list of atlas node ids
   */
  @JsonProperty("connectedMapIds")
  public List<Integer> getConnectedMapIds() {
    final List<ConnectedAtlasNode> connectedAtlasNodeList = getAtlasNodeKeys();

    if (connectedAtlasNodeList == null) {
      return null;
    }

    final List<Integer> idList = new ArrayList<>();

    for (final ConnectedAtlasNode node: getAtlasNodeKeys()) {
      idList.add(node.getId().getConnectedAtlasNodeId());
    }

    return idList;
  }

  /**
   * get name of map.
   * @return name of map
   */
  @JsonProperty("worldAreasName")
  public String getWorldAreasName() {
    return getWorldAreas().getName();
  }

  /**
   * get monster level of map.
   * @return level of map
   */
  @JsonProperty("worldAreasLevel")
  public int getWorldAreasLevel() {
    return getWorldAreas().getAreaLevel();
  }

  // /**
  //  * get tier level of map.
  //  * @return tier of map as int
  //  */
  // @JsonProperty("world_areas_tier")
  // public int getWorldAreasTier() {
  //   return getWorldAreas().getAreaLevel() - TIER_MAGIC_NUMBER;
  // }

  /**
   * get dds file path for regular map.
   * @return dds file path string
   */
  @JsonProperty("iconPath")
  public String getItemIconPath() {
    String iconPath = getDefaultItemVisualIdentityKey().getDdsFile();
    iconPath = iconPath.replaceFirst("\\.dds$", "");
    iconPath = StringUtils.join(iconPath, ".png");

    return iconPath;
  }

  /**
   * get dds file path for shaped map.
   * @return dds file path string
   */
  @JsonProperty("shapedIconPath")
  public String getShapedItemDds() {
    final ItemVisualIdentity key = getDefaultShapedItemVisualIdentityKey();

    if (key == null) {
      return null;
    }

    String iconPath = key.getDdsFile();
    iconPath = iconPath.replaceFirst("\\.dds$", "");
    iconPath = StringUtils.join(iconPath, ".png");

    return iconPath;
  }
}
