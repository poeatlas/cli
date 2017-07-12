package com.github.poeatlas.cli.dat;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Created by blei on 7/10/17.
 */
@Slf4j
@Data
@ToString(exclude = "buf")
public class DatReader {
  private static final long MAGIC_NUMBER = new BigInteger("BBBBBBBBBBBBBBBB", 16)
      .longValue();

  // ????
  private static final int TABLE_OFFSET = 4;

  private int magicOffset;

  private int tableLength;

  private int rows;

  private int rowLength;

  private final File file;

  private ByteBuffer buf;

  public DatReader(final File file) throws IOException {
    this.file = file;

    init();
  }

  private void init() throws IOException {
    final FileChannel fileChannel = FileChannel.open(file.toPath());
    final int fileLength = (int) file.length();
    final ByteBuffer buf = fileChannel.map(MapMode.READ_ONLY, 0, fileLength);
    final int iterCount = fileLength - 7;

    buf.order(ByteOrder.LITTLE_ENDIAN);

    int magicOffset = -1;
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

    setMagicOffset(magicOffset);

    // get first 4 bytes of file
    final int rows = buf.getInt();
    final int tableLength = magicOffset - TABLE_OFFSET;

    log.debug("rows = {}, tableLength = {}", rows, tableLength);

    if (rows < 1 || tableLength < 1) {
      throw new IOException("Zero or negative table row/length");
    }

    final int rowLength = (int) Math.floor((double) tableLength / rows);

    setTableLength(tableLength);
    setRows(rows);
    setRowLength(rowLength);
    setBuf(buf);

    log.info(toString());
  }

  public void test() {
    // final int offset = 4 + 2 * getRowLength();
    // //struct.unpack('<' + casts[0][2], self._file_raw[offset:offset+casts[0][1]])[0]
    // // [offset:offset+self.table_record_length]
    // final byte[] bytes = new byte[getRowLength()];
    // log.info("offset: {}", offset);
    // buf.position(offset);
    // buf.get(bytes);
    //
    // for (int i = 0; i < bytes.length; i++) {
    //   log.info("{}, {}", bytes[i], Character.toString((char)bytes[i]));
    // }
    buf.position(0);
    byte[] chars = new byte[30];
    for (int i = 0; i < 1000000000; i++) {
      buf.get(chars);
      log.info("{}", new String(chars));
    }
  }
}
