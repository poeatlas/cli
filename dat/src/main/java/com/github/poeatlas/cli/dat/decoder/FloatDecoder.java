package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class FloatDecoder  extends Decoder<Float> {
  private final static int COLUMN_LENGTH = 4;

  FloatDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Float decode(int id, ByteBuffer buf) {

    return buf.getFloat();
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}

