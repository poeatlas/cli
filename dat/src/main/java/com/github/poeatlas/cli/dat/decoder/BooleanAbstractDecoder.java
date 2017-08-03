package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class BooleanAbstractDecoder extends AbstractDecoder<Boolean> {
  private static final int COLUMN_LENGTH = 1;

  BooleanAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Boolean decode(final int id, final ByteBuffer buf) {
    return buf.get() == 1;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
