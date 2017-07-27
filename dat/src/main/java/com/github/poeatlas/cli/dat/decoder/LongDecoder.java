package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class LongDecoder extends Decoder<Long> {
  private final static int COLUMN_LENGTH = 8;

  LongDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Long decode(int id, ByteBuffer buf) {
    return 0L;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
