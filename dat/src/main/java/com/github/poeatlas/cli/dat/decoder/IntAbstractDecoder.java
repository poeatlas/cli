package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.util.DecoderUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
public class IntAbstractDecoder extends AbstractDecoder<Integer> {
  private static final int COLUMN_LENGTH = 4;

  IntAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Integer decode(final int id, final ByteBuffer buf) {
    final Integer value = buf.getInt();

    if (DecoderUtils.isNull(value)) {
      return null;
    }
    return value;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
