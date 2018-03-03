package com.github.poeatlas.cli.dat.json;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MapJson {
  private int id;
  private String name;
  private int level;
  private float x;
  private float y;
  private List<Integer> connected;
  private List<Integer> sextant;
  private String iconPath;
  private String shapedIconPath;
  private boolean isUnique;

  // public String getShapedIconPath() {
  //
  //   return shapedIconPath;
  // }
}
