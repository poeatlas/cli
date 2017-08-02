package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by NothingSoup on 7/13/17.
 * no FK dat dependencies
 */
@SuppressWarnings("PMD.TooManyFields")
@Entity
@Slf4j
@Data
@ToString
@Table(name = "item_visual_identity")
public class ItemVisualIdentity {
  @Id
  private long id;

  @Column(nullable = false)
  private String itemKey;

  @Column(nullable = false)
  private String ddsFile;

  // @Column(nullable = false)
  // private String aoFile;

  // @Transient
  // private long soundEffectsKey;
  //
  // @Column(nullable = false)
  // private int unknownUniqueInt;
  //
  // @Column(nullable = false)
  // private String aoFile2;
  //
  // @Transient
  // private List<String> marauderSmFiles;
  //
  // @Transient
  // private List<String> rangerSmFiles;
  //
  // @Transient
  // private List<String> witchSmFiles;
  //
  // @Transient
  // private List<String> duelistDexSmFiles;
  //
  // @Transient
  // private List<String> templarSmFiles;
  //
  // @Transient
  // private List<String> shadowSmFiles;
  //
  // @Transient
  // private List<String> scionSmFiles;
  //
  // @Column(nullable = false)
  // private String marauderShape;
  //
  // @Column(nullable = false)
  // private String rangerShape;
  //
  // @Column(nullable = false)
  // private String witchShape;
  //
  // @Column(nullable = false)
  // private String duelistShape;
  //
  // @Column(nullable = false)
  // private String templarShape;
  //
  // @Column(nullable = false)
  // private String shadowShape;
  //
  // @Column(nullable = false)
  // private String scionShape;
  //
  // @Column(nullable = false)
  // private int unknown28;
  //
  // @Column(nullable = false)
  // private int unknown29;
  //
  // @Transient
  // private List<Long> pickupAchievementItemsKeys;
  //
  // @Transient
  // private List<String> smFiles;
  //
  // @Transient
  // private List<Long> identifyAchievementItemsKeys;
  //
  // @Column(nullable = false)
  // private String epkFile;
  //
  // @Transient
  // private List<Long> corruptAchievementItemsKeys;
  //
  // @Column(nullable = false)
  // private boolean isAlternateArt;
  //
  // @Column(nullable = false)
  // private boolean flag2;
  //
  // @Column(nullable = false)
  // private long createCorruptedJewelAchievementItemsKey;
}
