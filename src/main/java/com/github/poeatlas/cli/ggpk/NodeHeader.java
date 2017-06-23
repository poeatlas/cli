package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString
class NodeHeader {
  public static final int NODE_HEADER_BYTE_SIZE = 8;

  /* bytes of the node (file). */
  @Getter @Setter
  private int length;

  /* the node (file) type. */
  @Getter @Setter
  private NodeTypes type;

  /* offset of this Node */
  @Getter @Setter
  private long offset;

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the file channel
   * @return the Node
   */
  @SneakyThrows
  protected void fill(final FileChannel fileChannel) {
    final ByteBuffer buf = ByteBuffer.allocate(8);

    // set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);

    // fill buffer + flip it for reading
    fileChannel.read(buf);
    buf.flip();

    // gets header length
    this.setLength(buf.getInt());

    // gets header type (is a 4 byte string)
    final byte[] fileType = new byte[4];
    buf.get(fileType);
    this.setType(NodeTypes.valueOf(fileType));
  }

}
