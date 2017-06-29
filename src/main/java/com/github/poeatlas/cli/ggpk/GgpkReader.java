package com.github.poeatlas.cli.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString(callSuper = true)
public class GgpkReader {

  /* inputFile we are reading */
  @Getter
  @Setter
  private final File inputFile;

  /**
   * constructor - processes ggpk file to find root and root children.
   * @param inputFile - file to read
   * @throws IOException - if file is invalid
   */
  public GgpkReader(final File inputFile) throws IOException {
    this.inputFile = inputFile;

    initialize();
  }

  /**
   * set and check GGPK file.
   * @throws IOException - if file is invalid
   */
  private void initialize() throws IOException {
    if (!inputFile.exists() || inputFile.isDirectory()) {
      throw new IOException("file invalidi");
    }

    final FileChannel fileChannel = FileChannel.open(inputFile.toPath());
    final NodeHeader nodeHeader = new NodeHeader();

    nodeHeader.fill(fileChannel);

    if (nodeHeader.getType() != NodeTypes.GGPK) {
      throw new IOException("invalid GGPK type");
    }
  }

  /**
   * finds root node and children.
   * @param output - results of ggpk file read
   * @throws IOException - if file is invalid
   */
  public void writeTo(final File output) throws IOException {
    final FileChannel fileChannel = FileChannel.open(inputFile.toPath());
    final List<Long> partitionOffsets = getPartitionOffsets(fileChannel);
    final NodeHeader ggpkPartition = new NodeHeader();

    // itereate through nodes to find children and offsets
    for (final long offset : partitionOffsets) {
      ggpkPartition.fill(fileChannel, offset);

      if (ggpkPartition.getType() == NodeTypes.PDIR) {
        break;
      }
    }

    if (ggpkPartition.getType() != NodeTypes.PDIR) {
      throw new IOException("no PDIR found");
    }

    final RootNode rootNode = RootNode.from(fileChannel, ggpkPartition.getOffset());
    if (!rootNode.getName().equals("")) {
      // System.out.println("Name of Root Directory is not blank");

    }
    // System.out.println(rootNode);
  }

  /**
   * get offets of the partition.
   * @param fileChannel - input FileChannel to read
   * @return list long partitons
   * @throws IOException - if file is invalid
   */
  private List<Long> getPartitionOffsets(final FileChannel fileChannel) throws IOException {
    ByteBuffer buf = ByteBuffer.allocate(12);

    // set order to little endian to read ints correctly
    buf.order(LITTLE_ENDIAN);

    // fill buffer + flip it for reading
    fileChannel.read(buf);

    // gets # of children - skipping first 8 as that contains node header info
    final int childrenCount = buf.getInt(8);

    buf = ByteBuffer.allocate(Long.BYTES * childrenCount);
    buf.order(LITTLE_ENDIAN);

    // read inputFile + flip it after for reading
    fileChannel.read(buf);
    buf.flip();

    final List<Long> offsets = new ArrayList<>();
    // gather up all the children offsets
    for (int i = 0; i < childrenCount; i++) {
      offsets.add(buf.getLong());
    }

    return offsets;
  }
}
