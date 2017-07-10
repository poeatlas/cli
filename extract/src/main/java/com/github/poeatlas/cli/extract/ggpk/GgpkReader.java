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
import java.util.List;
import java.util.Objects;
import java.util.Queue;

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

  private final Queue<DataNode> queue = new ArrayDeque<>(50);


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

    final Meta meta = Meta.builder()
        .withChannel(fileChannel)
        .build();

    if (meta.getType() != NodeTypes.GGPK) {
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
    final Meta.MetaBuilder partitionMetaBuilder = Meta.builder().withChannel(fileChannel);
    Meta partitionMeta = null;

    log.trace(partitionOffsets.toString());

    // iterate through nodes to find children and offsets
    for (final long offset : partitionOffsets) {
      partitionMeta = partitionMetaBuilder.withOffset(offset).build();

      log.debug(partitionMeta.toString());

      if (partitionMeta.getType() == NodeTypes.PDIR) {
        log.info("Found the Root GGPK Partition.");
        break;
      }
    }

    Objects.requireNonNull(partitionMeta, "no partition meta could be found.");

    if (partitionMeta.getType() != NodeTypes.PDIR) {
      throw new IOException("no PDIR found");
    }

    final DirectoryNode rootNode = DirectoryNode.builder()
        .withChannel(fileChannel)
        .withOffset(partitionMeta.getOffset())
        .build();

    log.debug(rootNode.toString());

    // final RootNode rootNode = RootNode.from(fileChannel, ggpkPartition.getOffset());
    final String rootName = rootNode.getName();
    if (!rootName.isEmpty()) {
      throw new IOException("Name of Root Directory is not blank: " + rootName);
    }

    queue.add(rootNode);

    processQueue(output);
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void processQueue(final File output) throws IOException {
    log.info("Starting to process GGPK files...");

    final NodeFilter filter = new NodeFilter();

    while (!queue.isEmpty()) {
      final DataNode dataNode = queue.poll();

      final NodeTypes type = dataNode.getType();
      if (log.isDebugEnabled()) {
        log.debug(dataNode.toString());
      }

      if (type == NodeTypes.PDIR) {
        final DirectoryNode node = dataNode.asDirectoryNode();
        final String path = node.getPath();
        final File outFile = new File(output, path);

        if ((!outFile.exists() || outFile.exists() && !outFile.isDirectory())
            && !outFile.mkdirs()) {
          throw new IOException("Could not create directory: " + path);
        }

        // find children nodes and add to queue
        for (final long offset : node.getChildOffsets()) {
          final DataNode childNode = processDirectoryNode(offset);
          childNode.setPath(path + "/" + childNode.getName());
          if (filter.doFilter(childNode)) {
            queue.add(childNode);
          }
        }
      } else if (type == NodeTypes.FILE) {
        final FileNode node = dataNode.asFileNode();
        final File outFile = new File(output, node.getPath());

        // find contents of file, get bytes, and extract
        extractFileNode(node, outFile);

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
    final Meta meta = Meta.builder()
        .withChannel(fileChannel)
        .withOffset(offset)
        .build();

    if (log.isDebugEnabled()) {
      log.debug(meta.toString());
    }

    final NodeTypes nodeType = meta.getType();

    switch (nodeType) {
      case PDIR:
        return DirectoryNode.builder()
            .withOffset(meta.getOffset())
            .withChannel(fileChannel)
            .build();
      case FILE:
        return FileNode.builder()
            .withOffset(meta.getOffset())
            .withChannel(fileChannel)
            .withNodeHeader(meta)
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
