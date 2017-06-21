package com.github.poeatlas.cli;

import static java.lang.System.out;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.SneakyThrows;

import java.io.File;


public class Main {
  public static void main(final String[] args) {
    System.exit(new Main().hello(args));
  }

  @SneakyThrows
  private int hello(final String[] args) {
    final OptionParser parser = new OptionParser();

    parser.accepts("input", "The GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    parser.accepts("output", "Where to extract the contents of the GGPK file.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSet opt = parser.parse(args);

    if (!opt.has("input") || !opt.has("output")) {
      parser.printHelpOn(out);
      return 1;
    }

    return 0;
  }
}
