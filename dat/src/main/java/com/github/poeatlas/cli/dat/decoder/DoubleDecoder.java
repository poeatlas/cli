package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.nio.ByteBuffer;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class DoubleDecoder extends Decoder<Double> {
  private final static int COLUMN_LENGTH = 4;

  @Override
  public Double decode(ByteBuffer buf, DatMeta meta) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
