package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
public class IntDecoder extends Decoder<Integer> {
  private static final int COLUMN_LENGTH = 4;

  IntDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Integer decode(int id, ByteBuffer buf) {
    return 0;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
