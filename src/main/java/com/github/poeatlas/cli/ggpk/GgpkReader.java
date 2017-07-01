package com.github.poeatlas.cli.ggpk;

import static com.github.poeatlas.cli.enums.NodeTypes.FILE;
import static com.github.poeatlas.cli.enums.NodeTypes.PDIR;
import static java.lang.System.out;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.enums.NodeTypes;
import lombok.Data;
import lombok.SneakyThrows;
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
  private FileChannel fileChannel;

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
      throw new IOException("file invalid");
    }

    fileChannel = FileChannel.open(inputFile.toPath());

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

    final DirectoryNode rootNode = DirectoryNode.builder()
        .withChannel(fileChannel)
        .withOffset(ggpkPartition.getOffset())
        .build();

    // final RootNode rootNode = RootNode.from(fileChannel, ggpkPartition.getOffset());
    final String rootName = rootNode.getName();
    if (!rootName.isEmpty()) {
      throw new IOException("Name of Root Directory is not blank: " + rootName);
    }


    NodeQueue.add(rootNode);

    processQueue(output);

    // processDirectoryNode(fileChannel, rootNode.getChildOffsets(), rootDirectory);

  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void processQueue(final File output) throws IOException {
    while (!NodeQueue.isEmpty()) {
      final DataNode dataNode = NodeQueue.poll();

      final NodeTypes type = dataNode.getType();

      out.println(dataNode);

      if (type == PDIR) {
        final DirectoryNode node = dataNode.asDirectoryNode();
        final String path = node.getPath();
        final File outFile = new File(output, path);

        if ((!outFile.exists() || outFile.exists() && !outFile.isDirectory())
            && !outFile.mkdirs()) {
          throw new IOException("Could not create directory: " + path);
        }

        // find children nodes and add to queue
        node.getChildOffsets()
            .stream()
            .map(this::processChildNode)
            .forEach(childNode -> {
              childNode.setPath(path + "/" + childNode.getName());
              NodeQueue.add(childNode);
            });
      } else if (type == FILE) {
        // FileNode node = dataNode.asFileNode();
        // File outFile = new File(output, node.getPath());

        // find contents of file, get bytes, and extract
      } else {
        // something went wrong, explode
      }
    }

  }

  @SneakyThrows
  private DataNode processChildNode(final long offset) {
    final NodeHeader nodeHeader = new NodeHeader();

    fileChannel.position(offset);

    // fill node header information + calculate its offset
    nodeHeader.fill(fileChannel, offset);

    final NodeTypes nodeType = nodeHeader.getType();

    switch (nodeType) {
      case PDIR:
        return DirectoryNode.builder()
            .withOffset(nodeHeader.getOffset())
            .withChannel(fileChannel)
            .build();
      case FILE:
        throw new IOException("TODO");
      default:
        throw new IOException("Invalid node type: " + nodeType);
    }
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
