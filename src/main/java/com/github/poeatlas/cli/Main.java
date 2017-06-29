package com.github.poeatlas.cli;

import static java.lang.System.out;

import com.github.poeatlas.cli.ggpk.GgpkReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;

import java.io.File;

public class Main {
  public static void main(final String[] args) {
    System.exit(new Main().processGgpk(args));
  }

  @SneakyThrows
  private int processGgpk(final String[] args) {
    final OptionParser parser = new OptionParser();

    final OptionSpec<File> input = parser.accepts("input", "The GGPK inputFile.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSpec<File> output = parser.accepts("output", "Where to extract the "
                                                             + "contents of the GGPK inputFile.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSet opt = parser.parse(args);

    if (!opt.has(input) || !opt.has(output)) {
      parser.printHelpOn(out);
      return 1;
    }

    final File inputFile = opt.valueOf(input);
    final File outputFile = opt.valueOf(output);

    final GgpkReader ggpkReader = new GgpkReader(inputFile);
    ggpkReader.writeTo(outputFile);

    return 0;
  }
}
