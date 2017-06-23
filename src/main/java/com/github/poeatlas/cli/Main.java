package com.github.poeatlas.cli;

import static java.lang.System.out;

import com.github.poeatlas.cli.enums.NodeTypes;
import com.github.poeatlas.cli.ggpk.ChildNode;
import com.github.poeatlas.cli.ggpk.RootNode;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.channels.FileChannel;

public class Main {
  public static void main(final String[] args) {
    System.exit(new Main().hello(args));
  }

  @SneakyThrows
  private int hello(final String[] args) {
    final OptionParser parser = new OptionParser();

    final OptionSpec<File> input = parser.accepts("input", "The GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSpec<File> output = parser.accepts("output", "Where to extract the "
                                                             + "contents of the GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSet opt = parser.parse(args);

    if (!opt.has(input) || !opt.has(output)) {
      parser.printHelpOn(out);
      return 1;
    }

    final File inputFile = opt.valueOf(input);
    final FileChannel fileChannel = FileChannel.open(inputFile.toPath());

    final RootNode rootNode = RootNode.valueOf(fileChannel);
    out.println(rootNode);
    if (rootNode.getType() != NodeTypes.GGPK) {
      return 1;
    }

    for (final long offset : rootNode.getOffsets()) {
      out.println(ChildNode.valueOf(fileChannel, offset));
    }

    return 0;
  }
}
