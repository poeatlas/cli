package com.github.poeatlas.cli.dat.decoder;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.MODULE)
public class StringDecoder extends Decoder<String> {

  @Override
  public Pair<String, Integer> decode(final ByteBuffer buf, final DatMeta datMeta, final int initialOffset) {
    final int stringOffset = buf.getInt(initialOffset);
    final int beginOffset = datMeta.getMagicOffset() + stringOffset;
    final int bytesToRead = beginOffset + datMeta.getTableRowLength();

    // value end offset when we find x00 x00 x00 x00
    int endOffset = 0;
    for (int i = beginOffset; i < bytesToRead; i += 2) {
      buf.position(i);
      if (buf.getInt(i) == 0) {
        endOffset = i;
        break;
      }
    }

    String value;
    // string is empty
    if (beginOffset == endOffset) {
      value = "";
    } else {
      // check for string ending in x00 and another starting with x00

      // if (buf.getInt(endOffset + 1) == 0) {
      //   endOffset = endOffset + 1;
      // }

      // convert value into string + remove terminating char
      final int nameLength = endOffset - beginOffset + 1;
      final char[] nameBuf = new char[nameLength];
      buf.order(LITTLE_ENDIAN);
      buf.position(beginOffset);
      buf.asCharBuffer().get(nameBuf);
      value = new String(nameBuf);
      final int nameTermination = value.indexOf('\0');

      if (nameTermination != -1) {
        value = value.substring(0, nameTermination);
      }
    }
    // log.info("value: {}", value);
    return Pair.of(value, endOffset);
  }
}
