package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.util.DecoderUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class FloatAbstractDecoder extends AbstractDecoder<Float> {
  private static final int COLUMN_LENGTH = 4;

  FloatAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Float decode(final int id, final ByteBuffer buf) {
    final Float value = buf.getFloat();

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

