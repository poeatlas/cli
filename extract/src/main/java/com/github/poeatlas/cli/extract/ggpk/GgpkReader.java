package com.github.poeatlas.cli.extract.ggpk;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.extract.enums.NodeTypes;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by NothingSoup on 6/22/17.
 */
@ToString(callSuper = true)
@Data
@Slf4j
public class GgpkReader {
  /* inputFile we are reading */
  private final File inputFile;
  private FileChannel fileChannel;

  private final Deque<DataNode> dequeue = new ArrayDeque<>(50);


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
    @Cleanup final FileChannel fileChannel = FileChannel.open(inputFile.toPath());
    final List<Long> partitionOffsets = getPartitionOffsets(fileChannel);
    final NodeHeader ggpkPartition = new NodeHeader();

    log.trace(partitionOffsets.toString());

    // iterate through nodes to find children and offsets
    for (final long offset : partitionOffsets) {
      ggpkPartition.fill(fileChannel, offset);

      log.debug(ggpkPartition.toString());

      if (ggpkPartition.getType() == NodeTypes.PDIR) {
        log.info("Found the Root GGPK Partition.");
        break;
      }
    }

    if (ggpkPartition.getType() != NodeTypes.PDIR) {
      throw new IOException("no PDIR found");
    }

    final DirectoryNode rootNode = DirectoryNode.builder()
        .withChannel(fileChannel)
        .withOffset(ggpkPartition.getOffset())
        .build();

    log.debug(rootNode.toString());

    // final RootNode rootNode = RootNode.from(fileChannel, ggpkPartition.getOffset());
    final String rootName = rootNode.getName();
    if (!rootName.isEmpty()) {
      throw new IOException("Name of Root Directory is not blank: " + rootName);
    }

    dequeue.addLast(rootNode);

    processQueue(output);
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void processQueue(final File output) throws IOException {
    log.info("Starting to process GGPK files...");

    final NodeFilter filter = new NodeFilter();

    while (!dequeue.isEmpty()) {
      final DataNode dataNode = dequeue.pollLast();

      final NodeTypes type = dataNode.getType();
      if (log.isDebugEnabled()) {
        log.debug(dataNode.toString());
      }

      if (type == NodeTypes.PDIR) {
        final DirectoryNode node = dataNode.asDirectoryNode();
        final String path = node.getPath();
        final File outFile = new File(output, path);

        if (filter.directoryFilter(node)
            && ((!outFile.exists() || outFile.exists() && !outFile.isDirectory())
            && !outFile.mkdirs())) {
          throw new IOException("Could not create directory: " + path);
        }

        // find children nodes and add to dequeue
        for (final long offset : node.getChildOffsets()) {
          final DataNode childNode = processDirectoryNode(offset);
          childNode.setPath(path + "/" + childNode.getName());
          dequeue.addLast(childNode);
        }
      } else if (type == NodeTypes.FILE) {
        final FileNode node = dataNode.asFileNode();
        final File outFile = new File(output, node.getPath());

        // find contents of file, get bytes, and extract
        if (filter.fileFilter(node)) {
          extractFileNode(node, outFile);
        }
      } else {
        // something went wrong, explode
        throw new IOException("not a PDIR or a FILE type");
      }
    }

    log.info("Finished processing GGPK files.");
  }

  private void extractFileNode(final FileNode node, final File outFile) throws IOException {
    @Cleanup final FileChannel outputChannel = FileChannel.open(outFile.toPath(),
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE);

    fileChannel.transferTo(node.getContentOffset(), node.getSize(), outputChannel);
  }

  @SneakyThrows
  private DataNode processDirectoryNode(final long offset) {
    final NodeHeader nodeHeader = new NodeHeader();

    fileChannel.position(offset);

    // fill node header information + calculate its offset
    nodeHeader.fill(fileChannel, offset);

    if (log.isDebugEnabled()) {
      log.debug(nodeHeader.toString());
    }

    final NodeTypes nodeType = nodeHeader.getType();

    switch (nodeType) {
      case PDIR:
        return DirectoryNode.builder()
            .withOffset(nodeHeader.getOffset())
            .withChannel(fileChannel)
            .build();
      case FILE:
        return FileNode.builder()
            .withOffset(nodeHeader.getOffset())
            .withChannel(fileChannel)
            .withNodeHeader(nodeHeader)
            .build();
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
