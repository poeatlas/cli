package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class BooleanDecoder extends Decoder<Boolean> {
  private final static int COLUMN_LENGTH = 4;

  BooleanDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Boolean decode(int id, ByteBuffer buf) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
