package com.github.poeatlas.cli.extract.ggpk;

import com.github.poeatlas.cli.extract.enums.NodeTypes;
import lombok.Data;
import lombok.ToString;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@ToString
class DataNode {
  private String name;

  private String path;

  private String digest;

  // private long offset;

  private NodeTypes type;

  public final DirectoryNode asDirectoryNode() {
    return (DirectoryNode) this;
  }

  public final FileNode asFileNode() {
    return (FileNode) this;
  }
}
