package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class ReferenceDecoder extends Decoder<Object> {
  @Override
  public Object decode(ByteBuffer buf, DatMeta meta) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return 0;
  }
}
