package com.github.poeatlas.cli.dat;

import com.github.poeatlas.cli.dat.decoder.Decoder;
import com.github.poeatlas.cli.dat.domain.WorldAreas;
import com.github.poeatlas.cli.dat.repository.WorldAreasRepository;
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
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;

/**
 * Created by blei on 7/10/17.
 */
@Component
@Slf4j
@Data
@ToString(exclude = "buf")
public class DatReader {

  @Autowired
  WorldAreasRepository worldAreasRepo;

  private static final long MAGIC_NUMBER = new BigInteger("BBBBBBBBBBBBBBBB", 16)
      .longValue();

  // ????
  private static final int TABLE_OFFSET = 4;

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
    final int tableLength = magicOffset - TABLE_OFFSET;

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

    List<Field> worldAreasFieldList = new ArrayList<>();
    for (Field field : WorldAreas.class.getDeclaredFields()) {
      // add non static and non-transient fields into list for parsing
      if (!Modifier.isStatic(field.getModifiers())
          && java.util.Objects.isNull(field.getAnnotation(Transient.class))) {
        worldAreasFieldList.add(field);
      }
    }
    WorldAreas worldAreas = new WorldAreas();
    PropertyAccessor worldAreasAccessor = PropertyAccessorFactory.forBeanPropertyAccess(worldAreas);

    for (Field field : worldAreasFieldList) {
      if (field.getName() == "id") {
        continue;
      }
      // log.info("curr field: {}, type: {}", field.getName(),field.getType());
      Decoder decoder = Decoder.getDecoder(field.getType());
      Pair<Object, Integer> decodedValue;
      for (int currRow = 0; currRow < datMeta.getTableRows(); currRow++) {
        final int initialOffset = 4 + currRow * datMeta.getTableRowLength();

        decodedValue = decoder.decode(buf, datMeta, initialOffset);
        worldAreasAccessor.setPropertyValue(field.getName(), decodedValue.getLeft());
        log.info(worldAreas.toString());
      }
    }


    // // get directory node name
    // final char[] nameBuf = new char[nameLength];
    //
    // buf = ByteBuffer.allocate(nameLength * 2);
    // buf.order(LITTLE_ENDIAN);
    //
    // channel.read(buf);
    //
    // buf.flip();
    // buf.asCharBuffer().get(nameBuf);
    // String name = new String(nameBuf);
    //
    // final int nameTermination = name.indexOf('\0');
    // if (nameTermination != -1) {
    //   name = name.substring(0, nameTermination);
    // }

    //
    // log.info("num bytes = {}", newOffset - getMagicOffset());
    // for (int i = getMagicOffset(); i < newOffset; i++) {
    //   log.info("string char is: {}",buf.get(i + 1));
    // }
    // }
    // }

    // final int offset = 4 + 2 * getRowLength();
    // //struct.unpack('<' + casts[0][2], self._file_raw[offset:offset+casts[0][1]])[0]
    // // [offset:offset+self.table_record_length]
    // final byte[] bytes = new byte[getRowLength()];
    // log.info("offset: {}", offset);
    // buf.position(offset);
    // buf.get(bytes);
    //
    // for (int i = 0; i < bytes.length; i++) {
    //   log.info("{}, {}", bytes[i], Character.toString((char)bytes[i]));
    // }
    // buf.position(0);
    // byte[] chars = new byte[30];
    // for (int i = 0; i < 1000000000; i++) {
    //   buf.get(chars);
    //   log.info("{}", new String(chars));
    // }
  }
}
