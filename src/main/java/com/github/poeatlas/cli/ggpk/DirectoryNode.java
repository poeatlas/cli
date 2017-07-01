package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import com.github.poeatlas.cli.utils.TransformUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class DirectoryNode extends DataNode {
  public static final String EMPTY_UTF16_STR = String.valueOf(new char[]{'\0', '\0'});

  private List<Long> childOffsets;

  public DirectoryNode() {
    super();
    setType(NodeTypes.PDIR);
  }

  public static DirectoryNodeBuilder builder() {
    return new DirectoryNodeBuilder();
  }

  /**
   * Created by blei on 6/30/17.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class DirectoryNodeBuilder {
    private FileChannel channel;
    private long offset;

    public final DirectoryNodeBuilder withOffset(final long offset) {
      this.offset = offset;
      return this;
    }

    public final DirectoryNodeBuilder withChannel(final FileChannel channel) {
      this.channel = channel;
      return this;
    }

    @SneakyThrows
    public final DirectoryNode build() {
      channel.position(offset);

      ByteBuffer buf = ByteBuffer.allocate(40);

      //set order to little endian to read ints correctly
      buf.order(LITTLE_ENDIAN);
      // fill buffer + flip it for reading
      channel.read(buf);
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
      final char[] nameBuf = new char[nameLength];

      buf = ByteBuffer.allocate(nameLength << 1);
      buf.order(LITTLE_ENDIAN);

      channel.read(buf);

      buf.flip();
      buf.asCharBuffer().get(nameBuf);
      String name = new String(nameBuf);

      final int nameTermination = name.indexOf('\0');
      if (nameTermination != -1) {
        name = name.substring(0, nameTermination);
      }

      if (name.length() == 1 && name.charAt(0) == 0) {
        name = "";
      }

      // get offsets of children nodes
      buf = ByteBuffer.allocate(12 * childCount);
      buf.order(LITTLE_ENDIAN);
      channel.read(buf);
      buf.flip();

      final List<Long> childOffsets = new ArrayList<>();
      for (int i = 0; i < childCount; i++) {
        childOffsets.add(buf.getLong(i * 12 + 4));
      }

      final DirectoryNode node = new DirectoryNode();
      node.setDigest(digest);
      node.setPath(""); // for you, PMD.
      node.setName(name);
      node.setOffset(offset);
      node.setChildOffsets(childOffsets);
      node.setChannel(channel);

      return node;

    }
  }
}
