package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class WrappedDecoder extends Decoder<Object> {
  WrappedDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Object decode(int id, ByteBuffer buf) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return 0;
  }
}
