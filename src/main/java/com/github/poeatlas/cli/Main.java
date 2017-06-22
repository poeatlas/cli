package com.github.poeatlas.cli;

import static java.lang.System.out;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Main {
  public static void main(final String[] args) {
    System.exit(new Main().hello(args));
  }

  @SneakyThrows
  private int hello(final String[] args) {
    final OptionParser parser = new OptionParser();

    OptionSpec<File> input = parser.accepts("input", "The GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    OptionSpec<File> output = parser.accepts("output", "Where to extract the contents of the GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSet opt = parser.parse(args);

    if (!opt.has("input") || !opt.has("output")) {
      parser.printHelpOn(out);
      return 1;
    }
    File inputFile = opt.valueOf(input);
    FileChannel fileChannel = null;

    fileChannel = FileChannel.open(inputFile.toPath());
//    byte[] headerBytes = new byte[4];
    // ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
    int len = 0;
    ByteBuffer words = ByteBuffer.allocate(4);
    words.order(ByteOrder.LITTLE_ENDIAN);
    fileChannel.read(words);
    words.flip();

//    out.println(words.getInt());

    byte[] tagArr = new byte[4];
    ByteBuffer tag = ByteBuffer.wrap(tagArr);
    fileChannel.read(tag);

    ByteBuffer nodeCount = ByteBuffer.allocate(4);
    nodeCount.order(ByteOrder.LITTLE_ENDIAN);
    fileChannel.read(nodeCount);
    nodeCount.flip();

    int nodeIntCount = nodeCount.getInt();
    int nodeOffset = 8 * nodeIntCount ;
    ByteBuffer offset = ByteBuffer.allocate(nodeOffset);
    offset.order(ByteOrder.LITTLE_ENDIAN);
    fileChannel.read(offset);
    offset.flip();


    for(int i = 0; i < nodeIntCount; i++) {
      fileChannel.position(offset.getLong(i*8));

      byte[] headerArr = new byte[8];
      ByteBuffer header = ByteBuffer.wrap(headerArr);
      fileChannel.read(header);
      out.println(new String(headerArr));

    }

    fileChannel.close();
    return 0;
  }
}
