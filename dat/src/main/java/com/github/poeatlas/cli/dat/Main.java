package com.github.poeatlas.cli.dat;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by NothingSoup on 7/9/17.
 */
@Slf4j
public class Main {
  /**
   * reads dat files.
   *
   * @param args file
   * @throws IOException if file is invalid
   */
  public static void main(final String[] args) throws IOException {
    final StopWatch stopWatch = StopWatch.createStarted();
    final Main app = new Main();

    if (log.isDebugEnabled()) {
      log.debug("args: {}", Arrays.asList(args));
    }

    try {
      app.run(new File(args[0]));
      log.info("Elapsed Time: {}", stopWatch.toString());
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      log.info("Elapsed Time: {}", stopWatch.toString());
      System.exit(1);
    }
  }

  /**
   * parses dat files and inputs into mysql db.
   *
   * @param file file(s) we are reading
   * @throws IOException if file is invalid
   */
  private void run(final File file) throws IOException {
    final DatReader reader = new DatReader(file);
  }
}
