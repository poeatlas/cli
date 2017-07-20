package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class ListDecoder extends Decoder<List<?>> {
  @Override
  public List<?> decode(ByteBuffer buf, DatMeta meta) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return 0;
  }
}
