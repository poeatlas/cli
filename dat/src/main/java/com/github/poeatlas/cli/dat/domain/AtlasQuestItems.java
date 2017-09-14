package com.github.poeatlas.cli.dat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Slf4j
@Data
@ToString
@Table(name = "atlas_quest_items")
public class AtlasQuestItems {

  @Id
  private int id;

  @JsonIgnore
  @Transient
  private long baseItemTypesKey;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "world_areas_id", referencedColumnName = "id", updatable = false)
  private WorldAreas worldAreas;

  @JsonIgnore
  @Transient
  private int questFlags;

  @JsonIgnore
  @Transient
  private int leagueQuestFlags;

  @Column(nullable = false)
  private int mapTier;

}