package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class LongAbstractDecoder extends AbstractDecoder<Long> {
  private static final int COLUMN_LENGTH = 8;
  // private static final Number NULL_UNSIGNED_LONG =
  //     new BigInteger("FFFFFFFFFFFFFFFF", 16).longValue();
  // private static final Number NULL_SIGNED_LONG =
  //     new BigInteger("FEFEFEFEFEFEFEFE", 16).longValue();

  LongAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Long decode(final int id, final ByteBuffer buf) {
    final Long value = buf.getLong();
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
  //   return value == NULL_UNSIGNED_LONG || value == NULL_SIGNED_LONG;
  // }
}
