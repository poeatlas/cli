package com.github.poeatlas.cli.ggpk;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FileNode extends DataNode {
  public FileNode() {
    super();
    setType(NodeTypes.FILE);
  }
}
