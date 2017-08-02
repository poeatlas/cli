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
 * No FK dat dependencies.
 */
@SuppressWarnings("PMD.TooManyFields")
@Entity
@Slf4j
@Data
@ToString
@Table(name = "world_areas")
public class WorldAreas {
  @Id
  private long id;

  @Column(nullable = false)
  private String areaKey;

  @Column(nullable = false)
  private String name;

  // @Column(nullable = false)
  // private int act;
  //
  // @Column(nullable = false)
  // private boolean isTown;
  //
  // @Column(nullable = false)
  // private boolean hasWaypoint;
  //
  // @Transient
  // private List<Integer> connectedWorldAreasKeys;
  //
  // @Column(nullable = false)
  // private int areaLevel;
  //
  // @Column(nullable = false)
  // private boolean hasDoodads;
  //
  // @Column(nullable = false)
  // private int unknown6;
  //
  // @Column(nullable = false)
  // private int unknown7;
  //
  // @Column(nullable = false)
  // private int unknown8;
  //
  // @Column(nullable = false)
  // private int unknown9;
  //
  // @Column(nullable = false)
  // private String loadingScreenDdsFile;
  //
  // @Column(nullable = false)
  // private int unknown11;
  //
  // @Transient
  // private List<Integer> data1;
  //
  // @Column(nullable = false)
  // private int unknown15;
  //
  // @Transient
  // private List<Long> topologiesKeys;
  //
  // @Transient
  // private int parentTownWorldAreasKey;
  //
  // @Column(nullable = false)
  // private long difficultiesKey;
  //
  // @Column(nullable = false)
  // private int unknown21;
  //
  // @Column(nullable = false)
  // private int unknown22;
  //
  // @Column(nullable = false)
  // private int unknown23;
  //
  // @Transient
  // private List<Long> bossesMonsterVarietiesKeys;
  //
  // @Transient
  // private List<Long> monstersMonsterVarietiesKeys;
  //
  // @Transient
  // private List<Long> spawnWeightTagsKeys;
  //
  // @Transient
  // private List<Integer> spawnWeightValues;
  //
  // @Column(nullable = false)
  // private boolean isMap;
  //
  // @Transient
  // private List<Long> fullClearAchievementItemsKeys;
  //
  // @Column(nullable = false)
  // private int unknown32;
  //
  // @Column(nullable = false)
  // private int unknown33;
  //
  // @Column(nullable = false)
  // private long achievementItemsKey;
  //
  // @Transient
  // private List<Long> modsKeys;
  //
  // @Column(nullable = false)
  // private int unknown39;
  //
  // @Column(nullable = false)
  // private int unknown40;
  //
  // @Transient
  // private List<Integer> vaalAreaWorldAreasKeys;
  //
  // @Column(nullable = false)
  // private int vaalAreaSpawnChance;
  //
  // @Column(nullable = false)
  // private int strongboxSpawnChance;
  //
  // @Column(nullable = false)
  // private int strongboxMaxCount;
  //
  // @Transient
  // private List<Integer> strongboxRarityWeight;
  //
  // @Column(nullable = false)
  // private boolean isTownArea;
  //
  // @Column(nullable = false)
  // private int unknown49;
  //
  // @Column(nullable = false)
  // private int maxLevel;
  //
  // @Transient
  // private List<Long> areaTypeTagsKeys;
  //
  // @Column(nullable = false)
  // private int unknown50;
  //
  // @Column(nullable = false)
  // private boolean isMercilessVaalArea;
  //
  // @Column(nullable = false)
  // private int unknown52;
  //
  // @Column(nullable = false)
  // private boolean isHideout;
  //
  // @Column(nullable = false)
  // private int unknown53;
  //
  // @Column(nullable = false)
  // private int unknown54;
  //
  // @Column(nullable = false)
  // private int unknown55;
  //
  // @Column(nullable = false)
  // private int unknown56;
  //
  // @Column(nullable = false)
  // private String unknownIndex;
  //
  // @Column(nullable = false)
  // private int unknown59;
  //
  // @Column(nullable = false)
  // private int unknown60;
  //
  // @Column(nullable = false)
  // private int unknown61;
  //
  // @Transient
  // private List<Long> tagsKeys;
  //
  // @Column(nullable = false)
  // private boolean isVaalArea;
  //
  // @Column(nullable = false)
  // private int unknown64;
  //
  // @Column(nullable = false)
  // private int unknown65;
  //
  // @Column(nullable = false)
  // private int unknown66;
  //
  // @Column(nullable = false)
  // private boolean isLabyrinthAirlock;
  //
  // @Column(nullable = false)
  // private boolean isLabyrinthArea;
  //
  // @Column(nullable = false)
  // private long twinnedFullClearAchievementItemsKey;
  //
  // @Column(nullable = false)
  // private long enterAchievementItemsKey;
  //
  // @Column(nullable = false)
  // private int unknown73;
  //
  // @Column(nullable = false)
  // private int unknown74;
  //
  // @Column(nullable = false)
  // private int unknown75;
  //
  // @Column(nullable = false)
  // private String unknown76;
  //
  // @Column(nullable = false)
  // private long eightModsFullClearAchievementItemsKey;
  //
  // @Column(nullable = false)
  // private int unknown77;
  //
  // @Column(nullable = false)
  // private int unknown78;
  //
  // @Column(nullable = false)
  // private int unknown79;
  //
  // @Transient
  // private List<Long> keys1;
  //
  // @Column(nullable = false)
  // private boolean flag0;
  //
  // @Column(nullable = false)
  // private boolean flag1;
  //
  // @Column(nullable = false)
  // private int unknown82;
  //
  // @Column(nullable = false)
  // private int unknown83;
  //
  // @Transient
  // private List<Long> keys2;
  //
  // @Column(nullable = false)
  // private long key0a;
  //
  // @Column(nullable = false)
  // private long key0b;
  //
  // @Column(nullable = false)
  // private String unknown88;
  //
  // @Column(nullable = false)
  // private int unknown89;
  //
  // @Column(nullable = false)
  // private int unknown90;
  //
  // @Column(nullable = false)
  // private int unknown91;
  //
  // @Column(nullable = false)
  // private int unknown92;
  //
  // @Column(nullable = false)
  // private int unknown93;

}
