package com.github.poeatlas.cli.ggpk;

import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString(callSuper = true)
public class ChildNode extends NodeHeader {
  private ChildNode() {
    // cannot do manually
  }

  @Override
  protected void fill(final FileChannel fileChannel) {
    super.fill(fileChannel);
  }

  /**
   * Creates child node based on fileChannel at given offset.
   * @param fileChannel the file
   * @param offset offset position for the file
   * @return childNode
   */
  @SneakyThrows
  public static ChildNode valueOf(final FileChannel fileChannel, final long offset) {
    final ChildNode childNode = new ChildNode();

    // set file position to the node offset, then fill it
    fileChannel.position(offset);
    childNode.fill(fileChannel);
    childNode.setOffset(offset + NodeHeader.NODE_HEADER_BYTE_SIZE);

    return childNode;
  }
}
