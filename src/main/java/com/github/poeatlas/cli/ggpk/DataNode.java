package com.github.poeatlas.cli.ggpk;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@ToString(exclude = "channel")
class DataNode {
  private String name;

  private String path;

  private String digest;

  private long offset;

  private NodeTypes type;

  @Getter(AccessLevel.MODULE)
  @Setter(AccessLevel.MODULE)
  private FileChannel channel;

  public final DirectoryNode asDirectoryNode() {
    return (DirectoryNode) this;
  }

  public final FileNode asFileNode() {
    return (FileNode) this;
  }
}
