package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString(callSuper = true)
public class RootNode extends NodeHeader {
  /* number of child nodes. */
  @Getter  @Setter
  private int childrenCount;

  /* list of children offsets from file. */
  private final List<Long> offsets = new ArrayList<>();

  private RootNode() {
    // do nothing
  }

  public List<Long> getOffsets() {
    return Collections.unmodifiableList(offsets);
  }

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the file channel
   * @return the Node
   */
  @SneakyThrows
  @Override
  protected void fill(final FileChannel fileChannel) {
    super.fill(fileChannel);
    ByteBuffer buf = ByteBuffer.allocate(4);

    // set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);

    // fill buffer + flip it for reading
    fileChannel.read(buf);
    buf.flip();

    // gets # of children
    this.setChildrenCount(buf.getInt());

    buf = ByteBuffer.allocate(Long.BYTES * this.childrenCount);
    buf.order(LITTLE_ENDIAN);

    // read file + flip it after for reading
    fileChannel.read(buf);
    buf.flip();

    // gather up all the children offsets
    for (int i = 0; i < this.childrenCount; i++) {
      this.offsets.add(buf.getLong());
    }
  }

  /**
   * Creates root node based on fileChannel.
   * @param fileChannel the file
   * @return RootNode
   */
  public static RootNode valueOf(final FileChannel fileChannel) {
    final RootNode rootNode = new RootNode();
    rootNode.fill(fileChannel);
    return rootNode;
  }
}
