package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.utils.TransformUtil;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@Builder
public class DirectoryNode {

  private String path;

  private String name;

  private String digest;

  private final List<DirectoryNode> subFolders = new ArrayList<>();

  private final List<FileNode> files = new ArrayList<>();

  private long offset;

  /**
   * Given a FileChannel, reads from current position to create a Node object
   * and its children nodes that it points to.
   *
   * @param fileChannel the inputFile channel
   * @return the Node
   */
  protected static DirectoryNode buildFrom(final FileChannel fileChannel,
                                           final long offset,
                                           final String previousPath) throws IOException {
    fileChannel.position(offset);
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
    final String digest = TransformUtil.digest(digestString);

    // get directory node name
    buf = ByteBuffer.allocate(nameLength << 1);
    final byte[] nameBuf = new byte[nameLength << 1];
    fileChannel.read(buf);
    buf.flip();
    buf.get(nameBuf);
    String name = new String(nameBuf, StandardCharsets.UTF_8);

    if (name.length() == 1 && name.charAt(0) == 0) {
      name = "";
    }

    // get offsets of children nodes
    buf = ByteBuffer.allocate(12 * childCount);
    buf.order(LITTLE_ENDIAN);
    fileChannel.read(buf);
    buf.flip();

    //    for (int i = 0; i < childCount; i++) {
    //      childOffsets.add(buf.getLong(i * 12 + 4));
    //    }
    final DirectoryNode directoryNode = DirectoryNode.builder()
        .path(previousPath + "/" + name)
        .name(name)
        .digest(digest)
        .offset(offset)
        .build();

    return directoryNode;
  }
}
