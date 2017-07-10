package com.github.poeatlas.cli.extract;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.poeatlas.cli.extract.ggpk.GgpkReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Main {
  /**
   * Processes GGPK files.
   */
  public static void main(final String[] args) throws IOException {
    final StopWatch stopWatch = StopWatch.createStarted();
    final Main app = new Main();

    try {
      app.processGgpk(args);
      stopWatch.stop();
      log.info("Elapsed Time: {}", stopWatch.toString());
    } catch (Exception ex) {
      log.error("Failed to complete extraction of GGPK file.", ex);
      log.info("Elapsed Time: {}", stopWatch.toString());
      System.exit(1);
    }
  }

  private void processGgpk(final String[] args) throws IOException {
    final OptionParser parser = new OptionParser("vv");

    final OptionSpec<File> input = parser.accepts("input", "The GGPK inputFile.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSpec<File> output = parser.accepts("output", "Where to extract the "
                                                             + "contents of the GGPK inputFile.")
        .withRequiredArg()
        .ofType(File.class);

    final OptionSpec<Level> verbose = parser.accepts("verbose", "Level of "
                                                                 + "verbosity to output.")
        .withRequiredArg()
        .ofType(Level.class);

    final OptionSet opt = parser.parse(args);

    // sets the verbosity of output for everything
    if (opt.has(verbose)) {
      final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      root.setLevel(opt.valueOf(verbose));
    }

    if (!opt.has(input) || !opt.has(output)) {
      parser.printHelpOn(System.out);
      return;
    }

    final File inputFile = opt.valueOf(input);
    final File outputFile = opt.valueOf(output);

    log.info("Input file is: {}", inputFile.getPath());
    log.info("Output directory is: {}", outputFile.getPath());

    final GgpkReader ggpkReader = new GgpkReader(inputFile);
    ggpkReader.writeTo(outputFile);
  }
}
