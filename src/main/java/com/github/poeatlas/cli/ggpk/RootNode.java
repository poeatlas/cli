package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.utils.TransformUtil;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NothingSoup on 6/27/17.
 */
@ToString
@Data
public class RootNode {
  // some hash for verifying node
  private String digest;

  // name of directory
  private String name;

  // directories of child, containing offset and timestamp
  private final List<Long> childOffsets = new ArrayList<>();

  private RootNode() {
    // do nothing
  }

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the inputFile channel
   * @return the Node
   */
  protected void fill(final FileChannel fileChannel) throws IOException {
    ByteBuffer buf = ByteBuffer.allocate(40);

    //set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);
    // fill buffer + flip it for reading
    fileChannel.read(buf);
    buf.flip();

    // gets name length
    final int nameLength = buf.getInt();

    // get childCount
    final int childCount = buf.getInt();

    // get digest of directory node
    final byte[] digestString = new byte[32];
    buf.get(digestString);
    this.setDigest(TransformUtil.digest(digestString));

    // get directory node name
    buf = ByteBuffer.allocate(nameLength << 1);
    final byte[] nameBuf = new byte[nameLength << 1];
    fileChannel.read(buf);
    buf.flip();
    buf.get(nameBuf);
    this.setName(new String(nameBuf, StandardCharsets.UTF_8));

    if (this.getName().length() == 1 && this.getName().charAt(0) == 0) {
      this.setName("");
    }

    // get offsets of children nodes
    buf = ByteBuffer.allocate(12 * childCount);
    buf.order(LITTLE_ENDIAN);
    fileChannel.read(buf);
    buf.flip();

    for (int i = 0; i < childCount; i++) {
      childOffsets.add(buf.getLong(i * 12 + 4));
    }
  }

  /**
   * processes and fills digest, name and offset of node.
   * @param fileChannel - input FileChannel to read from
   * @param offset - long value that determines where the node is in file
   * @return a RootNode object containing node's digest, name, and children offsets
   */
  public static RootNode from(final FileChannel fileChannel, final long offset) throws IOException {
    fileChannel.position(offset);
    final RootNode rootNode = new RootNode();
    rootNode.fill(fileChannel);
    return rootNode;
  }
}
