package com.github.poeatlas.cli.ggpk;

import lombok.Builder;
import lombok.Data;

import java.io.File;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@Builder
public class FileNode {
  private String path;

  private String name;

  private String digest;

  private long size;

  private long offset;

  private File originalFile;
}
