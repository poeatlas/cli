package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@Slf4j
public class IntAbstractDecoder extends AbstractDecoder<Integer> {
  private static final int COLUMN_LENGTH = 4;
  // private static final Number NULL_UNSIGNED_INT = new BigInteger("FFFFFFFF", 16).intValue();
  // private static final Number NULL_SIGNED_INT = new BigInteger("FEFEFEFE", 16).intValue();

  IntAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Integer decode(final int id, final ByteBuffer buf) {
    final Integer value = buf.getInt();

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
  //   return value == NULL_UNSIGNED_INT || value == NULL_SIGNED_INT;
  // }
}
