package com.github.poeatlas.cli.ggpk;

import static com.github.poeatlas.cli.enums.NodeTypes.PDIR;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Data;
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
@Data
public class GgpkReader {

  /* inputFile we are reading */
  private final File inputFile;

  /**
   * constructor - processes ggpk file to find root and root children.
   *
   * @param inputFile - file to read
   * @throws IOException - if file is invalid
   */
  public GgpkReader(final File inputFile) throws IOException {
    this.inputFile = inputFile;

    initialize();
  }

  /**
   * set and check GGPK file.
   *
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
   *
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

      if (ggpkPartition.getType() == PDIR) {
        break;
      }
    }

    if (ggpkPartition.getType() != PDIR) {
      throw new IOException("no PDIR found");
    }

    final RootNode rootNode = RootNode.from(fileChannel, ggpkPartition.getOffset());
    if (!rootNode.getName().equals("")) {
      // System.out.println("Name of Root Directory is not blank");
    }

    final DirectoryNode rootDirectory = DirectoryNode.builder()
        .path("")
        .name(rootNode.getName())
        .digest(rootNode.getDigest())
        .offset(ggpkPartition.getOffset())
        .build();

    processNode(fileChannel, rootNode.getChildOffsets(), rootDirectory);
  }

  /**
   * Determines type of current node and calls respective processing method.
   *
   * @param fileChannel      channel for ggpk input file
   * @param partitionOffsets array of longs of child offsets for the node
   * @param directoryNode    directory node of parent--contains the children nodes we want ot check
   * @throws IOException issue with filechannel
   */
  private void processNode(final FileChannel fileChannel,
                           final List<Long> partitionOffsets,
                           final DirectoryNode directoryNode) throws IOException {
    final NodeHeader nodeHeader = new NodeHeader();
    for (final long offset : partitionOffsets) {
      fileChannel.position(offset);

      // fill node header information + calculate its offset
      nodeHeader.fill(fileChannel, offset);

      final NodeTypes nodeType = nodeHeader.getType();

      switch (nodeType) {
        case PDIR:
          processDirectoryNode(fileChannel, directoryNode, nodeHeader);
          break;
        case FILE:
          break;
        default:
      }
    }
  }

  /**
   * Derives a directory node of current node.
   *
   * @param fileChannel         channel of input ggpk file
   * @param parentDirectoryNode directory node of the parent of current node
   * @param nodeHeader          header information of current node
   * @throws IOException issue with filechannel
   */
  private void processDirectoryNode(final FileChannel fileChannel,
                                    final DirectoryNode parentDirectoryNode,
                                    final NodeHeader nodeHeader) throws IOException {

    // TODO: complete this method!
    // create DirectoryNode from filechannel's offset of current node
    /* final DirectoryNode directoryNode = */ DirectoryNode.buildFrom(fileChannel,
        nodeHeader.getOffset(),
        parentDirectoryNode.getPath());
  }

  /**
   * get offets of the partition.
   *
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
