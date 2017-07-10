package com.github.poeatlas.cli.dds;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
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
    final Main app = new Main();
    try {
      app.run(args);
    } catch (Exception ex) {
      log.error("Unable to extract files.");
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

    final OptionSpec<Integer> mipmap = parser.accepts("mipmap", "Mipmap number for dds")
        .withRequiredArg()
        .ofType(Integer.class)
        .defaultsTo(0);

    final OptionSpec<File> files = parser.nonOptions().ofType(File.class);

    final OptionSet opt = parser.parse(args);

    // create + set mipmap for DdsExtractor
    final DdsExtractor extractor = new DdsExtractor(opt.valueOf(mipmap));

    // loop through file list and extract to png
    final List<File> fileList = opt.valuesOf(files);
    for (final File file : fileList) {
      extractor.extract(file);
    }

    // log.info("All args: {}", Arrays.asList(args));
    // log.info("{}", opt.valuesOf(files));

  }

}
