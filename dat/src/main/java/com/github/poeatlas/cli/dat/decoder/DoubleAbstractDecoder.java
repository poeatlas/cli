package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class DoubleAbstractDecoder extends AbstractDecoder<Double> {
  private static final int COLUMN_LENGTH = 4;
  // private static final Number NULL_DOUBLE =
  //     new BigInteger("EFEFEFEFEFEFEFEF", 16).doubleValue();

  DoubleAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Double decode(final int id, final ByteBuffer buf) {
    final Double value = buf.getDouble();

    // Long convertValue = ((ByteBuffer) ByteBuffer.allocate(8).putDouble(value).flip()).getLong();

    // if (isNull(value)) {
    //   return null;
    // }
    return value;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }

  // @Override
  // public boolean isNull(Number value) {
  //   return value == NULL_DOUBLE;
  // }
}
