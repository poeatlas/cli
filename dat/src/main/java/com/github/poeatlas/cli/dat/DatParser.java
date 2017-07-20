package com.github.poeatlas.cli.dat;

import com.github.poeatlas.cli.dat.annotation.Spec;
import com.github.poeatlas.cli.dat.decoder.Decoder;
import com.github.poeatlas.cli.dat.util.SpecUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NothingSoup on 7/19/17.
 */

@Slf4j
@Data
public class DatParser<T> {
  private static final long MAGIC_NUMBER = new BigInteger("BBBBBBBBBBBBBBBB", 16)
      .longValue();

  private static final int TABLE_START_OFFSET = 4;

  private final File file;

  private Class<T> clazz;

  private DatMeta datMeta;

  private ByteBuffer buf;

  public DatParser(final File directory, final Class<T> clazz) throws IOException {
    this.file = new File(directory, clazz.getSimpleName() + ".dat");
    this.clazz = clazz;

    init();
  }

  private void init() throws IOException {

    final FileChannel fileChannel = FileChannel.open(file.toPath());
    final int fileLength = (int) file.length();
    final ByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
    final int iterCount = fileLength - 7;

    buf.order(ByteOrder.LITTLE_ENDIAN);

    int magicOffset = -1;
    for (int i = 0; i < iterCount; i++) {
      final long data = buf.getLong(i);
      if (data == MAGIC_NUMBER) {
        magicOffset = i;
        break;
      }
    }

    log.debug("magic offset: {}", magicOffset);
    if (magicOffset == -1) {
      throw new IOException(file.getPath() + " is not a GGG .dat directory");
    }

    // get first 4 bytes of directory
    final int rows = buf.getInt();
    final int tableLength = magicOffset - TABLE_START_OFFSET;

    log.debug("rows = {}, tableLength = {}", rows, tableLength);

    if (rows < 1 || tableLength < 1) {
      throw new IOException("Zero or negative table row/length");
    }

    final int rowLength = (int) Math.floor((double) tableLength / rows);

    setBuf(buf);

    log.info(toString());

    datMeta = DatMeta.builder()
        .magicOffset(magicOffset)
        .tableLength(tableLength)
        .tableRowLength(rowLength)
        .tableRows(rows)
        .build();
  }
  /**
   * WIP.
   */
  public List<T> parse() throws IOException, IllegalAccessException, InstantiationException {

    final List<Pair<Field, Spec>> specList = SpecUtils.getSpec(clazz);
    final String idName = SpecUtils.getId(clazz).getName();
    final int tableEndOffset = datMeta.getMagicOffset();
    final int tableRowLength = datMeta.getTableRowLength();
    final List<T> valueList = new ArrayList<>();

    for (int position = 4, id = 0; position < tableEndOffset; position += tableRowLength, id++) {
      final Map<String, Object> props = new HashMap<>();
      int nextPosition = position;

      // set id value
      props.put(idName, id);
      buf.position(position);

      // final int initialOffset = 4 + position * datMeta.getTableRowLength();

      for (Pair<Field, Spec> pair: specList) {
        final Field field = pair.getKey();
        final Spec spec = pair.getValue();
        final Decoder decoder = Decoder.getDecoder(spec.value());
        final Object decodedValue = decoder.decode(buf, datMeta);

        props.put(field.getName(), decodedValue);

        nextPosition += decoder.getColumnLength();
        buf.position(nextPosition);
      }

      final T record = clazz.newInstance();
      final PropertyAccessor recordAccessor =
          PropertyAccessorFactory.forBeanPropertyAccess(record);

      recordAccessor.setPropertyValues(props);
      valueList.add(record);

      log.info(record.toString());
      // worldAreasRepo.save(worldAreas);
    }
    return valueList;
  }
}