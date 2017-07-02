package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString
@Data
class NodeHeader {
  public static final int NODE_HEADER_BYTE_SIZE = 8;

  /* bytes of the node (inputFile). */
  // @Getter
  // @Setter
  private int length;

  /* the node (inputFile) type. */
  @Getter
  @Setter
  private NodeTypes type;

  /* offset of this Node */
  @Getter
  @Setter
  private long offset;

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the inputFile channel
   * @return the Node
   */

  public void fill(final FileChannel fileChannel) throws IOException {
    fill(fileChannel, 0);
  }

  public void fill(final FileChannel fileChannel, final long offset) throws IOException {
    if (offset > 0) {
      // set inputFile position to the node offset, then fill it
      fileChannel.position(offset);
      this.setOffset(offset + NodeHeader.NODE_HEADER_BYTE_SIZE);
    }

    final ByteBuffer buf = ByteBuffer.allocate(8);

    // set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);

    // fill buffer + flip it for reading
    fileChannel.read(buf);
    buf.flip();

    // gets header length
    this.setLength(buf.getInt());
    // buf.position(buf.position()+4);

    // gets header type (is a 4 byte string)
    final byte[] fileType = new byte[4];
    buf.get(fileType);
    this.setType(NodeTypes.valueOf(fileType));
  }
}
