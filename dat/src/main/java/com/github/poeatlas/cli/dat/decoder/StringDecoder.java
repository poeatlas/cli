package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@NoArgsConstructor(access = AccessLevel.MODULE)
public class StringDecoder extends Decoder<String> {
  @Override
  public String decode(ByteBuffer buf, DatMeta meta) {
    return null;
  }
}
