package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@NoArgsConstructor(access = AccessLevel.MODULE)
public class IntDecoder extends Decoder<Integer> {
  @Override
  public Pair<Integer, Integer> decode(final ByteBuffer buf, final DatMeta meta, final int beginOffset) {
    return null;
  }
}
