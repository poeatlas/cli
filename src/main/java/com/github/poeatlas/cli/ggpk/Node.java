package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by blei on 6/22/17.
 */
@ToString
public class Node {
  /* bytes of the node (file). */
  @Getter
  private int length;

  /* the node (file) type. */
  @Getter
  private NodeTypes type;

  /* number of child nodes. */
  @Getter
  private int childrenCount;

  /* list of children offsets from file. */
  private final List<Long> childrenOffsets = new ArrayList<>();

  private Node() {
    // do nothing
  }

  public List<Long> getChildrenOffsets() {
    return Collections.unmodifiableList(childrenOffsets);
  }

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the file channel
   * @return the Node
   */
  @SneakyThrows
  public static Node valueOf(final FileChannel fileChannel) {
    ByteBuffer buf = ByteBuffer.allocate(12);

    // set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);

    // fill buffer + flip it for reading
    fileChannel.read(buf);
    buf.flip();

    // now that it's read, create the Node.
    final Node header = new Node();

    // gets header length
    header.length = buf.getInt();

    // gets header type (is a 4 byte string)
    final byte[] fileType = new byte[4];
    buf.get(fileType);
    header.type = NodeTypes.valueOf(fileType);

    // gets # of children
    header.childrenCount = buf.getInt();

    buf = ByteBuffer.allocate(Long.BYTES * header.childrenCount);
    buf.order(LITTLE_ENDIAN);

    // read file + flip it after for reading
    fileChannel.read(buf);
    buf.flip();

    // gather up all the children offsets
    for (int i = 0; i < header.childrenCount; i++) {
      header.childrenOffsets.add(buf.getLong());
    }

    return header;
  }

}
