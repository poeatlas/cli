package com.github.poeatlas.cli.dds;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by blei on 7/6/17.
 */
@Slf4j
public class Main {
  /**
   * extract dds images.
   * @param args cli args
   * @throws IOException invalid argument
   */
  public static void main(final String[] args) throws IOException {
    final StopWatch stopWatch = StopWatch.createStarted();
    final Main app = new Main();

    if (log.isDebugEnabled()) {
      log.debug("args: {}", Arrays.asList(args));
    }

    try {
      app.run(args);
      log.info("Elapsed Time: {}", stopWatch.toString());
    } catch (Exception ex) {
      log.error("Unable to extract files.", ex);
      log.info("Elapsed Time: {}", stopWatch.toString());
      System.exit(1);
    }
  }

  /**
   * parse args for files and mipmap. then extract.
   * @param args cli args
   * @throws IOException invalid argument
   */
  public void run(final String[] args) throws IOException {
    final OptionParser parser = new OptionParser();

    final OptionSpec<Integer> mipmapSpec = parser.accepts("mipmap", "Mipmap number for dds")
        .withRequiredArg()
        .ofType(Integer.class)
        .defaultsTo(0);

    final OptionSpec<File> rootSpec = parser.accepts("root", "Root path of dds")
        .withRequiredArg()
        .ofType(File.class)
        .required();

    final OptionSpec<File> files = parser.nonOptions().ofType(File.class);
    final OptionSet opt = parser.parse(args);
    final int mipmap = opt.valueOf(mipmapSpec);
    final File root = opt.valueOf(rootSpec);

    // create + set mipmap for DdsExtractor
    final DdsExtractor extractor = new DdsExtractor(mipmap, root);

    log.info("DDS Extractor will extract all files using mipmap {}.", mipmap);

    // loop through file list and extract to png
    final List<File> fileList = opt.valuesOf(files);
    for (final File file : fileList) {
      extractor.extract(file);
    }
  }

}
