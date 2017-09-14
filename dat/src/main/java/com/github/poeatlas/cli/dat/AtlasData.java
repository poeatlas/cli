package com.github.poeatlas.cli.dat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AtlasData {

  private int id;

  @JsonProperty("x")
  private float posX;

  @JsonProperty("y")
  private float posY;

  private List<Integer> connectedMapIds;

  private String worldAreasName;

  private int worldAreasLevel;

  private String iconPath;

  private String shapedIconPath;

  private Integer shaperOrb;

}
