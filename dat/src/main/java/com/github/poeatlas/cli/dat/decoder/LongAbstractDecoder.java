package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.util.DecoderUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class LongAbstractDecoder extends AbstractDecoder<Long> {
  private static final int COLUMN_LENGTH = 8;

  LongAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
  }

  @Override
  public Long decode(final int id, final ByteBuffer buf) {
    final Long value = buf.getLong();
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
