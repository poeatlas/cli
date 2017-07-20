package com.github.poeatlas.cli.dat;

import com.github.poeatlas.cli.dat.annotation.Spec;
import com.github.poeatlas.cli.dat.decoder.Decoder;
import com.github.poeatlas.cli.dat.domain.WorldAreas;
import com.github.poeatlas.cli.dat.repository.WorldAreasRepository;
import com.github.poeatlas.cli.dat.util.SpecUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by blei on 7/10/17.
 */
@Component
@Slf4j
@Data
@ToString(exclude = "buf")
public class DatReader {
  @Autowired
  private WorldAreasRepository worldAreasRepo;

  private static final long MAGIC_NUMBER = new BigInteger("BBBBBBBBBBBBBBBB", 16)
      .longValue();

  private static final int TABLE_START_OFFSET = 4;

  private DatMeta datMeta;

  private File directory;

  private ByteBuffer buf;

  private void init() throws IOException {
    final File worldAreasDat = new File(directory, "WorldAreas.dat");

    final FileChannel fileChannel = FileChannel.open(worldAreasDat.toPath());
    final int fileLength = (int) worldAreasDat.length();
    final ByteBuffer buf = fileChannel.map(MapMode.READ_ONLY, 0, fileLength);
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
      throw new IOException(worldAreasDat.getPath() + " is not a GGG .dat directory");
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
  public void parseDat(@NonNull final File directory) throws IOException {
    if (directory.isDirectory()) {
      this.directory = directory;
    } else {
      throw new IOException(directory.getPath() + "is not a directory");
    }
    init();

    final List<Pair<Field, Spec>> worldAreasList = SpecUtils.getSpec(WorldAreas.class);
    final String idName = SpecUtils.getId(WorldAreas.class).getName();
    final int tableEndOffset = datMeta.getMagicOffset();
    final int tableRowLength = datMeta.getTableRowLength();

    for (int position = 4, id = 0; position < tableEndOffset; position += tableRowLength, id++) {
      final Map<String, Object> props = new HashMap<>();
      int nextPosition = position;

      // set id value
      props.put(idName, id);
      buf.position(position);

      // final int initialOffset = 4 + position * datMeta.getTableRowLength();

      for (Pair<Field, Spec> pair: worldAreasList) {
        final Field field = pair.getKey();
        final Spec spec = pair.getValue();
        final Decoder decoder = Decoder.getDecoder(spec.value());
        final Object decodedValue = decoder.decode(buf, datMeta);

        props.put(field.getName(), decodedValue);

        nextPosition += decoder.getColumnLength();
        buf.position(nextPosition);
      }

      final WorldAreas worldAreas = new WorldAreas();
      final PropertyAccessor worldAreasAccessor =
          PropertyAccessorFactory.forBeanPropertyAccess(worldAreas);
      worldAreasAccessor.setPropertyValues(props);

      log.info(worldAreas.toString());
      // worldAreasRepo.save(worldAreas);
    }
  }
}
