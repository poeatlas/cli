package com.github.poeatlas.cli.dat;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by NothingSoup on 7/9/17.
 */
@Slf4j
public class Main {
  // equiv to {0xbb,0xbb,0xbb,0xbb,0xbb,0xbb,0xbb,0xbb}
  private static final long MAGIC_NUMBER = -4919131752989213765L;

  private static final int TABLE_OFFSET = 4;

  /**
   * reads dat files.
   *
   * @param args file
   * @throws IOException if file is invalid
   */
  public static void main(final String[] args) throws IOException {
    final Main app = new Main();
    try {
      app.run(new File(args[0]));
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
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
    final FileChannel fileChannel = FileChannel.open(file.toPath());

    ByteBuffer buf = ByteBuffer.allocate(8);

    long fileMagicNum;
    // loop through bytes in file to try to find magic number
    do {
      fileChannel.read(buf);
      buf.flip();
      fileMagicNum = buf.getLong();
      fileChannel.position(fileChannel.position() - 7);
      buf.clear();
    }
    while (fileMagicNum != MAGIC_NUMBER);

    final long dataOffset = fileChannel.position() - 1;

    // get table rows
    buf = ByteBuffer.allocate(4);
    buf.order(LITTLE_ENDIAN);
    fileChannel.read(buf);
    buf.flip();

    final int tableRows = buf.getInt();
    final long tableLength = dataOffset - TABLE_OFFSET;

    // do no initialize
    double tableRecordLength = 0;
    if (tableRows > 0) {
      tableRecordLength = Math.floor((double) tableLength / tableRows);
    } else if (tableRows == 0 && tableLength == 0) {
      tableRecordLength = 0;
    }

    final long fileLength = file.length();
    log.info("{}", tableRecordLength);
    log.info("lengt: {} position: {}", fileLength, fileChannel.position());
  }
}
