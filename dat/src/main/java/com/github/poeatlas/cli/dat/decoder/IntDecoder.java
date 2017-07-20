package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@NoArgsConstructor(access = AccessLevel.MODULE)
public class IntDecoder extends Decoder<Integer> {
  private static final int COLUMN_LENGTH = 4;

  @Override
  public Integer decode(final ByteBuffer buf, final DatMeta meta) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
