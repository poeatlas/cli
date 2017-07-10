package com.github.poeatlas.cli.extract.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.extract.enums.NodeTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString
@Data
// @Slf4j
class Meta {
  public static final int NODE_HEADER_BYTE_SIZE = 8;

  /* bytes of the node (inputFile). */
  private int length;

  /* the node (inputFile) type. */
  private NodeTypes type;

  /* offset of this Node */
  private long offset;

  public static MetaBuilder builder() {
    return new MetaBuilder();
  }


  /**
   * Created by blei on 6/30/17.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class MetaBuilder {
    private FileChannel channel;
    private long offset;

    public final MetaBuilder withOffset(final long offset) {
      this.offset = offset;
      return this;
    }

    public final MetaBuilder withChannel(final FileChannel channel) {
      this.channel = channel;
      return this;
    }

    @SneakyThrows
    public final Meta build() {
      if (channel == null) {
        throw new IOException("Channel is null");
      }

      // set inputFile position to the node offset, then fill it
      if (offset > 0) {
        channel.position(offset);
      }

      final ByteBuffer buf = ByteBuffer.allocate(8);

      // set order to little endian to read ints correctly
      buf.order(LITTLE_ENDIAN);

      // fill buffer + flip it for reading
      channel.read(buf);
      buf.flip();

      // gets header length
      final int length = buf.getInt();

      // gets header type (is a 4 byte string)
      final byte[] fileType = new byte[4];
      buf.get(fileType);

      // create meta object
      final Meta meta = new Meta();
      meta.setLength(length);
      meta.setType(NodeTypes.valueOf(fileType));
      meta.setOffset(offset > 0 ? offset + Meta.NODE_HEADER_BYTE_SIZE : offset);

      return meta;
    }
  }
}
