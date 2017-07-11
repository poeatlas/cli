package com.github.poeatlas.cli.dat;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Created by blei on 7/10/17.
 */
@Slf4j
@Data
public class DatReader {
  private static final long MAGIC_NUMBER = new BigInteger("BBBBBBBBBBBBBBBB", 16)
      .longValue();

  // ????
  private static final int TABLE_OFFSET = 4;


  private final File file;

  public DatReader(final File file) throws IOException {
    this.file = file;

    init();
  }

  private void init() throws IOException {
    final FileChannel fileChannel = FileChannel.open(file.toPath());
    final int fileLength = (int) file.length();
    final ByteBuffer buf = fileChannel.map(MapMode.READ_ONLY, 0, fileLength);
    final int iterCount = fileLength - 7;

    long magicOffset = -1;
    for (int i = 0; i < iterCount; i++) {
      long data = buf.getLong(i);
      if (data == MAGIC_NUMBER) {
        magicOffset = i;
        break;
      }
    }

    log.debug("magic offset: {}", magicOffset);
    if (magicOffset == -1) {
      throw new IOException(file.getPath() + " is not a GGG .dat file");
    }

    //
    // // get table rows
    // buf = ByteBuffer.allocate(4);
    // buf.order(LITTLE_ENDIAN);
    // fileChannel.read(buf);
    // buf.flip();
    //
    // final int tableRows = buf.getInt();
    // final long tableLength = dataOffset - TABLE_OFFSET;
    //
    // // do no initialize
    // double tableRecordLength = 0;
    // if (tableRows > 0) {
    //   tableRecordLength = Math.floor((double) tableLength / tableRows);
    // } else if (tableRows == 0 && tableLength == 0) {
    //   tableRecordLength = 0;
    // }
    //
    // final long fileLength = file.length();
    // log.info("{}", tableRecordLength);
    // log.info("lengt: {} position: {}", fileLength, fileChannel.position());
  }
}
