package com.github.poeatlas.cli.dat.decoder;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by blei on 7/18/17.
 */
@Slf4j
public class StringDecoder extends Decoder<String> {
  private static final int COLUMN_LENGTH = 4;

  StringDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public String decode(int id, final ByteBuffer buf) {
    final DatMeta datMeta = getMeta();
    final int stringOffset = buf.getInt();
    final int beginOffset = datMeta.getMagicOffset() + stringOffset;
    final int bytesToRead = beginOffset + datMeta.getTableRowLength();

    // log.info("bytes to read: {}", bytesToRead);
    // log.info("begin offset: {}", beginOffset);
    // value end offset when we find x00 x00 x00 x00
    // for (int i = 0; i <= datMeta.getTableRowLength(); i++) {
    //   log.info("value: {}, offset: {}", buf.get(i + beginOffset), i + beginOffset);
    // }

    // value end offset when we find x00 x00 x00 x00
    int endOffset = 0;
    for (int i = beginOffset; i < bytesToRead; i += 2) {
      if (buf.getInt(i) == 0) {
        endOffset = i;
        break;
      }
    }

    final int valueLength = endOffset - beginOffset;

    // empty string; end it
    if (beginOffset == endOffset) {
      return "";
    }

    // check for string ending in x00 and another starting with x00

    // if (buf.getInt(endOffset + 1) == 0) {
    //   endOffset = endOffset + 1;
    // }

    // convert value into string + remove terminating char
    final char[] nameBuf = new char[valueLength / 2];

    buf.order(LITTLE_ENDIAN);
    buf.position(beginOffset);
    buf.asCharBuffer().get(nameBuf);

    String value = new String(nameBuf);
    final int nameTermination = value.indexOf('\0');

    if (nameTermination != -1) {
      value = value.substring(0, nameTermination);
    }

    return value;
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}
