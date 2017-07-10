package com.github.poeatlas.cli.extract.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.extract.enums.NodeTypes;
import com.github.poeatlas.cli.extract.utils.TransformUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/29/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FileNode extends DataNode {
  private long size;
  private long contentOffset;

  public FileNode() {
    super();
    setType(NodeTypes.FILE);
  }

  public static FileNodeBuilder builder() {
    return new FileNodeBuilder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class FileNodeBuilder {
    private FileChannel channel;
    private long offset;
    private Meta meta;

    public FileNodeBuilder withChannel(final FileChannel channel) {
      this.channel = channel;
      return this;
    }

    public FileNodeBuilder withOffset(final long offset) {
      this.offset = offset;
      return this;
    }

    public FileNodeBuilder withNodeHeader(final Meta meta) {
      this.meta = meta;
      return this;
    }

    /**
     * builds file node.
     *
     * @return FileNode containing file's size/offset for extraction
     */
    @SneakyThrows
    public final FileNode build() {
      if (channel == null) {
        throw new IOException("File Channel is null");
      }

      channel.position(offset);

      ByteBuffer buf = ByteBuffer.allocate(36);
      buf.order(LITTLE_ENDIAN);
      channel.read(buf);
      buf.flip();

      // get name length
      final int nameLength = buf.getInt();

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

      final long fileByteLength = 4 + 32 + nameLength * 2;
      final long fileSize = Meta.NODE_HEADER_BYTE_SIZE + fileByteLength;
      final long contentOffset = meta.getOffset() + fileByteLength;

      final FileNode fileNode = new FileNode();
      // fileNode.setOffset(offset);
      fileNode.setName(name);
      fileNode.setPath("");
      fileNode.setDigest(digest);
      fileNode.setSize((long) meta.getLength() - fileSize); // to ignore the meta bytes too
      fileNode.setContentOffset(contentOffset);
      return fileNode;
    }
  }
}
