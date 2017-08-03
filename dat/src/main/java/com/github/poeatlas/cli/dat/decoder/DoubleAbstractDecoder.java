package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.util.DecoderUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class DoubleAbstractDecoder extends AbstractDecoder<Double> {
  private static final int COLUMN_LENGTH = 4;

  DoubleAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Double decode(final int id, final ByteBuffer buf) {
    final Double value = buf.getDouble();

    // Long convertValue = ((ByteBuffer) ByteBuffer.allocate(8).putDouble(value).flip()).getLong();

    if (DecoderUtils.isNull(value)) {
      return null;
    }
    return value ;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
