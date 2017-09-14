package com.github.poeatlas.cli.dat;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AtlasData {

  private int id;

  private float posX;

  private float posY;

  private List<Integer> connectedMapIds;

  private String worldAreasName;

  private int worldAreasLevel;

  private String iconPath;

  private String shapedIconPath;

  private Integer shaperOrb;

}
